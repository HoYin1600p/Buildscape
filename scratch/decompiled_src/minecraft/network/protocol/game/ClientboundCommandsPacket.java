package net.minecraft.network.protocol.game;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiPredicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientboundCommandsPacket implements Packet {
   private static final StreamCodec ENTRY_STREAM_CODEC = StreamCodec.of((output, entry) -> entry.write(output), ClientboundCommandsPacket::readNode);
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ENTRY_STREAM_CODEC.apply(ByteBufCodecs.list()), (p) -> p.entries, ByteBufCodecs.VAR_INT, (p) -> p.rootIndex, ClientboundCommandsPacket::new);
   private static final byte MASK_TYPE = 3;
   private static final byte FLAG_EXECUTABLE = 4;
   private static final byte FLAG_REDIRECT = 8;
   private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
   private static final byte FLAG_RESTRICTED = 32;
   private static final byte TYPE_ROOT = 0;
   private static final byte TYPE_LITERAL = 1;
   private static final byte TYPE_ARGUMENT = 2;
   private final int rootIndex;
   private final List entries;

   public ClientboundCommandsPacket(final RootCommandNode root, final ClientboundCommandsPacket.NodeInspector inspector) {
      Object2IntMap nodeToId = enumerateNodes(root);
      this.entries = createEntries(nodeToId, inspector);
      this.rootIndex = nodeToId.getInt(root);
   }

   private ClientboundCommandsPacket(final List entries, final int rootIndex) {
      this.entries = entries;
      this.rootIndex = rootIndex;
      validateEntries(this.entries);
   }

   private static void validateEntries(final List entries, final BiPredicate validator) {
      IntSet elementsToCheck = new IntOpenHashSet(IntSets.fromTo(0, entries.size()));

      while(!elementsToCheck.isEmpty()) {
         boolean worked = elementsToCheck.removeIf((index) -> validator.test((ClientboundCommandsPacket.Entry)entries.get(index), elementsToCheck));
         if (!worked) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

   }

   private static void validateEntries(final List entries) {
      validateEntries(entries, ClientboundCommandsPacket.Entry::canBuild);
      validateEntries(entries, ClientboundCommandsPacket.Entry::canResolve);
   }

   private static Object2IntMap enumerateNodes(final RootCommandNode root) {
      Object2IntMap nodeToId = new Object2IntOpenHashMap();
      Queue queue = new ArrayDeque();
      queue.add(root);

      CommandNode node;
      while((node = (CommandNode)queue.poll()) != null) {
         if (!nodeToId.containsKey(node)) {
            int id = nodeToId.size();
            nodeToId.put(node, id);
            queue.addAll(node.getChildren());
            if (node.getRedirect() != null) {
               queue.add(node.getRedirect());
            }
         }
      }

      return nodeToId;
   }

   private static List createEntries(final Object2IntMap nodeToId, final ClientboundCommandsPacket.NodeInspector inspector) {
      ObjectArrayList result = new ObjectArrayList(nodeToId.size());
      result.size(nodeToId.size());
      ObjectIterator var3 = Object2IntMaps.fastIterable(nodeToId).iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)var3.next();
         result.set(entry.getIntValue(), createEntry((CommandNode)entry.getKey(), inspector, nodeToId));
      }

      return result;
   }

   private static ClientboundCommandsPacket.Entry readNode(final FriendlyByteBuf input) {
      byte flags = input.readByte();
      int[] children = input.readVarIntArray();
      int redirect = (flags & 8) != 0 ? input.readVarInt() : 0;
      ClientboundCommandsPacket.NodeStub stub = read(input, flags);
      return new ClientboundCommandsPacket.Entry(stub, flags, redirect, children);
   }

   private static ClientboundCommandsPacket.@Nullable NodeStub read(final FriendlyByteBuf input, final byte flags) {
      int type = flags & 3;
      if (type == 2) {
         String name = input.readUtf();
         int id = input.readVarInt();
         ArgumentTypeInfo argumentType = (ArgumentTypeInfo)BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId(id);
         if (argumentType == null) {
            return null;
         } else {
            ArgumentTypeInfo.Template argument = argumentType.deserializeFromNetwork(input);
            Identifier suggestionId = (flags & 16) != 0 ? input.readIdentifier() : null;
            return new ClientboundCommandsPacket.ArgumentNodeStub(name, argument, suggestionId);
         }
      } else if (type == 1) {
         String id = input.readUtf();
         return new ClientboundCommandsPacket.LiteralNodeStub(id);
      } else {
         return null;
      }
   }

   private static ClientboundCommandsPacket.Entry createEntry(final CommandNode node, final ClientboundCommandsPacket.NodeInspector inspector, final Object2IntMap ids) {
      int flags = 0;
      int redirect;
      if (node.getRedirect() != null) {
         flags |= 8;
         redirect = ids.getInt(node.getRedirect());
      } else {
         redirect = 0;
      }

      if (inspector.isExecutable(node)) {
         flags |= 4;
      }

      if (inspector.isRestricted(node)) {
         flags |= 32;
      }

      Objects.requireNonNull(node);
      byte var7 = 0;
      ClientboundCommandsPacket.NodeStub nodeStub;
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case 0:
            RootCommandNode ignored = (RootCommandNode)node;
            flags |= 0;
            nodeStub = null;
            break;
         case 1:
            ArgumentCommandNode arg = (ArgumentCommandNode)node;
            Identifier suggestionId = inspector.suggestionId(arg);
            nodeStub = new ClientboundCommandsPacket.ArgumentNodeStub(arg.getName(), ArgumentTypeInfos.unpack(arg.getType()), suggestionId);
            flags |= 2;
            if (suggestionId != null) {
               flags |= 16;
            }
            break;
         case 2:
            LiteralCommandNode literal = (LiteralCommandNode)node;
            nodeStub = new ClientboundCommandsPacket.LiteralNodeStub(literal.getLiteral());
            flags |= 1;
            break;
         default:
            throw new UnsupportedOperationException("Unknown node type " + String.valueOf(node));
      }

      int[] childrenIds = node.getChildren().stream().mapToInt(ids::getInt).toArray();
      return new ClientboundCommandsPacket.Entry(nodeStub, flags, redirect, childrenIds);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_COMMANDS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleCommands(this);
   }

   public RootCommandNode getRoot(final CommandBuildContext context, final ClientboundCommandsPacket.NodeBuilder builder) {
      return (RootCommandNode)(new ClientboundCommandsPacket.NodeResolver(context, builder, this.entries)).resolve(this.rootIndex);
   }

   private static record ArgumentNodeStub(String id, ArgumentTypeInfo.Template argumentType, @Nullable Identifier suggestionId) implements ClientboundCommandsPacket.NodeStub {
      public ArgumentBuilder build(final CommandBuildContext context, final ClientboundCommandsPacket.NodeBuilder builder) {
         ArgumentType type = this.argumentType.instantiate(context);
         return builder.createArgument(this.id, type, this.suggestionId);
      }

      public void write(final FriendlyByteBuf output) {
         output.writeUtf(this.id);
         serializeCap(output, this.argumentType);
         if (this.suggestionId != null) {
            output.writeIdentifier(this.suggestionId);
         }

      }

      private static void serializeCap(final FriendlyByteBuf output, final ArgumentTypeInfo.Template argumentType) {
         serializeCap(output, argumentType.type(), argumentType);
      }

      private static void serializeCap(final FriendlyByteBuf output, final ArgumentTypeInfo info, final ArgumentTypeInfo.Template argumentType) {
         output.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(info));
         info.serializeToNetwork(argumentType, output);
      }
   }

   private static record Entry(ClientboundCommandsPacket.@Nullable NodeStub stub, int flags, int redirect, int[] children) {
      public void write(final FriendlyByteBuf output) {
         output.writeByte(this.flags);
         output.writeVarIntArray(this.children);
         if ((this.flags & 8) != 0) {
            output.writeVarInt(this.redirect);
         }

         if (this.stub != null) {
            this.stub.write(output);
         }

      }

      public boolean canBuild(final IntSet unbuiltNodes) {
         if ((this.flags & 8) != 0) {
            return !unbuiltNodes.contains(this.redirect);
         } else {
            return true;
         }
      }

      public boolean canResolve(final IntSet unresolvedNodes) {
         for(int child : this.children) {
            if (unresolvedNodes.contains(child)) {
               return false;
            }
         }

         return true;
      }
   }

   private static record LiteralNodeStub(String id) implements ClientboundCommandsPacket.NodeStub {
      public ArgumentBuilder build(final CommandBuildContext context, final ClientboundCommandsPacket.NodeBuilder builder) {
         return builder.createLiteral(this.id);
      }

      public void write(final FriendlyByteBuf output) {
         output.writeUtf(this.id);
      }
   }

   public interface NodeBuilder {
      ArgumentBuilder createLiteral(String id);

      ArgumentBuilder createArgument(String id, ArgumentType argumentType, @Nullable Identifier suggestionId);

      ArgumentBuilder configure(ArgumentBuilder input, boolean executable, boolean restricted);
   }

   public interface NodeInspector {
      @Nullable Identifier suggestionId(ArgumentCommandNode node);

      boolean isExecutable(CommandNode node);

      boolean isRestricted(CommandNode node);
   }

   private static class NodeResolver {
      private final CommandBuildContext context;
      private final ClientboundCommandsPacket.NodeBuilder builder;
      private final List entries;
      private final List nodes;

      private NodeResolver(final CommandBuildContext context, final ClientboundCommandsPacket.NodeBuilder builder, final List entries) {
         this.context = context;
         this.builder = builder;
         this.entries = entries;
         ObjectArrayList nodes = new ObjectArrayList();
         nodes.size(entries.size());
         this.nodes = nodes;
      }

      public CommandNode resolve(final int index) {
         CommandNode currentNode = (CommandNode)this.nodes.get(index);
         if (currentNode != null) {
            return currentNode;
         } else {
            ClientboundCommandsPacket.Entry entry = (ClientboundCommandsPacket.Entry)this.entries.get(index);
            CommandNode result;
            if (entry.stub == null) {
               result = new RootCommandNode();
            } else {
               ArgumentBuilder resultBuilder = entry.stub.build(this.context, this.builder);
               if ((entry.flags & 8) != 0) {
                  resultBuilder.redirect(this.resolve(entry.redirect));
               }

               boolean isExecutable = (entry.flags & 4) != 0;
               boolean isRestricted = (entry.flags & 32) != 0;
               result = this.builder.configure(resultBuilder, isExecutable, isRestricted).build();
            }

            this.nodes.set(index, result);

            for(int childId : entry.children) {
               CommandNode child = this.resolve(childId);
               if (!(child instanceof RootCommandNode)) {
                  result.addChild(child);
               }
            }

            return result;
         }
      }
   }

   private interface NodeStub {
      ArgumentBuilder build(CommandBuildContext context, ClientboundCommandsPacket.NodeBuilder builder);

      void write(FriendlyByteBuf output);
   }
}

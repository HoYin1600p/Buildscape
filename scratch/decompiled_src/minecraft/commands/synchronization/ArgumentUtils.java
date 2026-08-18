package net.minecraft.commands.synchronization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
import org.slf4j.Logger;

public class ArgumentUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final byte NUMBER_FLAG_MIN = 1;
   private static final byte NUMBER_FLAG_MAX = 2;

   public static int createNumberFlags(final boolean hasMin, final boolean hasMax) {
      int result = 0;
      if (hasMin) {
         result |= 1;
      }

      if (hasMax) {
         result |= 2;
      }

      return result;
   }

   public static boolean numberHasMin(final byte flags) {
      return (flags & 1) != 0;
   }

   public static boolean numberHasMax(final byte flags) {
      return (flags & 2) != 0;
   }

   private static void serializeArgumentCap(final JsonObject result, final ArgumentTypeInfo info, final ArgumentTypeInfo.Template argumentType) {
      info.serializeToJson(argumentType, result);
   }

   private static void serializeArgumentToJson(final JsonObject result, final ArgumentType argument) {
      ArgumentTypeInfo.Template template = ArgumentTypeInfos.unpack(argument);
      result.addProperty("type", "argument");
      result.addProperty("parser", String.valueOf(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(template.type())));
      JsonObject type = new JsonObject();
      serializeArgumentCap(type, template.type(), template);
      if (!type.isEmpty()) {
         result.add("properties", type);
      }

   }

   public static JsonObject serializeNodeToJson(final CommandDispatcher dispatcher, final CommandNode node) {
      JsonObject result = new JsonObject();
      Objects.requireNonNull(node);
      byte path = 0;
      switch (node.typeSwitch<invokedynamic>(node, path)) {
         case 0:
            RootCommandNode rootNode = (RootCommandNode)node;
            result.addProperty("type", "root");
            break;
         case 1:
            LiteralCommandNode literalNode = (LiteralCommandNode)node;
            result.addProperty("type", "literal");
            break;
         case 2:
            ArgumentCommandNode argumentNode = (ArgumentCommandNode)node;
            serializeArgumentToJson(result, argumentNode.getType());
            break;
         default:
            LOGGER.error("Could not serialize node {} ({})!", node, node.getClass());
            result.addProperty("type", "unknown");
      }

      Collection children = node.getChildren();
      if (!children.isEmpty()) {
         JsonObject childrenObject = new JsonObject();

         for(CommandNode child : children) {
            childrenObject.add(child.getName(), serializeNodeToJson(dispatcher, child));
         }

         result.add("children", childrenObject);
      }

      if (node.getCommand() != null) {
         result.addProperty("executable", true);
      }

      Predicate var12 = node.getRequirement();
      if (var12 instanceof PermissionProviderCheck permissionCheck) {
         JsonElement permissions = (JsonElement)PermissionCheck.CODEC.encodeStart(JsonOps.INSTANCE, permissionCheck.test()).getOrThrow((error) -> new IllegalStateException("Failed to serialize requirement: " + error));
         result.add("permissions", permissions);
      }

      if (node.getRedirect() != null) {
         Collection path = dispatcher.getPath(node.getRedirect());
         if (!path.isEmpty()) {
            JsonArray target = new JsonArray();

            for(String piece : path) {
               target.add(piece);
            }

            result.add("redirect", target);
         }
      }

      return result;
   }

   public static Set findUsedArgumentTypes(final CommandNode node) {
      Set visitedNodes = new ReferenceOpenHashSet();
      Set result = new HashSet();
      findUsedArgumentTypes(node, result, visitedNodes);
      return result;
   }

   private static void findUsedArgumentTypes(final CommandNode node, final Set output, final Set visitedNodes) {
      if (visitedNodes.add(node)) {
         if (node instanceof ArgumentCommandNode) {
            ArgumentCommandNode arg = (ArgumentCommandNode)node;
            output.add(arg.getType());
         }

         node.getChildren().forEach((child) -> findUsedArgumentTypes(child, output, visitedNodes));
         CommandNode redirect = node.getRedirect();
         if (redirect != null) {
            findUsedArgumentTypes(redirect, output, visitedNodes);
         }

      }
   }
}

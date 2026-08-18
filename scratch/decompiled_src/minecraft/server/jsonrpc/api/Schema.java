package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.DiscoveryService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.Message;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.PlayerService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRuleType;

public record Schema(Optional reference, List type, Optional items, Map properties, List enumValues, Codec codec) {
   public static final Codec CODEC = Codec.recursive("Schema", (subCodec) -> RecordCodecBuilder.create((i) -> i.group(ReferenceUtil.REFERENCE_CODEC.optionalFieldOf("$ref").forGetter(Schema::reference), ExtraCodecs.compactListCodec(Codec.STRING).optionalFieldOf("type", List.of()).forGetter(Schema::type), subCodec.optionalFieldOf("items").forGetter(Schema::items), Codec.unboundedMap(Codec.STRING, subCodec).optionalFieldOf("properties", Map.of()).forGetter(Schema::properties), Codec.STRING.listOf().optionalFieldOf("enum", List.of()).forGetter(Schema::enumValues)).apply(i, (ref, type, items, properties, enumValues) -> null))).validate((schema) -> schema == null ? DataResult.error(() -> "Should not deserialize schema") : DataResult.success(schema));
   private static final List SCHEMA_REGISTRY = new ArrayList();
   public static final Schema BOOL_SCHEMA = ofType("boolean", Codec.BOOL);
   public static final Schema INT_SCHEMA = ofType("integer", Codec.INT);
   public static final Schema BOOL_OR_INT_SCHEMA = ofTypes(List.of("boolean", "integer"), Codec.either(Codec.BOOL, Codec.INT));
   public static final Schema NUMBER_SCHEMA = ofType("number", Codec.FLOAT);
   public static final Schema STRING_SCHEMA = ofType("string", Codec.STRING);
   public static final Schema UUID_SCHEMA = ofType("string", UUIDUtil.CODEC);
   public static final Schema DISCOVERY_SCHEMA = ofType("string", DiscoveryService.DiscoverResponse.CODEC.codec());
   public static final SchemaComponent DIFFICULTY_SCHEMA = registerSchema("difficulty", ofEnum(Difficulty::values, Difficulty.CODEC));
   public static final SchemaComponent GAME_TYPE_SCHEMA = registerSchema("game_type", ofEnum(GameType::values, GameType.CODEC));
   public static final Schema PERMISSION_LEVEL_SCHEMA = ofType("integer", PermissionLevel.INT_CODEC);
   public static final SchemaComponent PLAYER_SCHEMA = registerSchema("player", record(PlayerDto.CODEC.codec()).withField("id", UUID_SCHEMA).withField("name", STRING_SCHEMA));
   public static final SchemaComponent VERSION_SCHEMA = registerSchema("version", record(DiscoveryService.DiscoverInfo.CODEC.codec()).withField("name", STRING_SCHEMA).withField("protocol", INT_SCHEMA));
   public static final SchemaComponent SERVER_STATE_SCHEMA = registerSchema("server_state", record(ServerStateService.ServerState.CODEC).withField("started", BOOL_SCHEMA).withField("players", PLAYER_SCHEMA.asRef().asArray()).withField("version", VERSION_SCHEMA.asRef()));
   public static final Schema RULE_TYPE_SCHEMA = ofEnum(GameRuleType::values);
   public static final SchemaComponent TYPED_GAME_RULE_SCHEMA = registerSchema("typed_game_rule", record(GameRulesService.GameRuleUpdate.TYPED_CODEC).withField("key", STRING_SCHEMA).withField("value", BOOL_OR_INT_SCHEMA).withField("type", RULE_TYPE_SCHEMA));
   public static final SchemaComponent UNTYPED_GAME_RULE_SCHEMA = registerSchema("untyped_game_rule", record(GameRulesService.GameRuleUpdate.CODEC).withField("key", STRING_SCHEMA).withField("value", BOOL_OR_INT_SCHEMA));
   public static final SchemaComponent MESSAGE_SCHEMA = registerSchema("message", record(Message.CODEC).withField("literal", STRING_SCHEMA).withField("translatable", STRING_SCHEMA).withField("translatableParams", STRING_SCHEMA.asArray()));
   public static final SchemaComponent SYSTEM_MESSAGE_SCHEMA = registerSchema("system_message", record(ServerStateService.SystemMessage.CODEC).withField("message", MESSAGE_SCHEMA.asRef()).withField("overlay", BOOL_SCHEMA).withField("receivingPlayers", PLAYER_SCHEMA.asRef().asArray()));
   public static final SchemaComponent KICK_PLAYER_SCHEMA = registerSchema("kick_player", record(PlayerService.KickDto.CODEC.codec()).withField("message", MESSAGE_SCHEMA.asRef()).withField("player", PLAYER_SCHEMA.asRef()));
   public static final SchemaComponent OPERATOR_SCHEMA = registerSchema("operator", record(OperatorService.OperatorDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("bypassesPlayerLimit", BOOL_SCHEMA).withField("permissionLevel", INT_SCHEMA));
   public static final SchemaComponent INCOMING_IP_BAN_SCHEMA = registerSchema("incoming_ip_ban", record(IpBanlistService.IncomingIpBanDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("ip", STRING_SCHEMA).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));
   public static final SchemaComponent IP_BAN_SCHEMA = registerSchema("ip_ban", record(IpBanlistService.IpBanDto.CODEC.codec()).withField("ip", STRING_SCHEMA).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));
   public static final SchemaComponent PLAYER_BAN_SCHEMA = registerSchema("user_ban", record(BanlistService.UserBanDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));

   public static Codec typedCodec() {
      return CODEC;
   }

   public Schema info() {
      return new Schema(this.reference, this.type, this.items.map(Schema::info), (Map)this.properties.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (b) -> ((Schema)b.getValue()).info())), this.enumValues, this.codec);
   }

   private static SchemaComponent registerSchema(final String name, final Schema schema) {
      SchemaComponent entry = new SchemaComponent(name, ReferenceUtil.createLocalReference(name), schema);
      SCHEMA_REGISTRY.add(entry);
      return entry;
   }

   public static List getSchemaRegistry() {
      return SCHEMA_REGISTRY;
   }

   public static Schema ofRef(final URI ref, final Codec codec) {
      return new Schema(Optional.of(ref), List.of(), Optional.empty(), Map.of(), List.of(), codec);
   }

   public static Schema ofType(final String type, final Codec codec) {
      return ofTypes(List.of(type), codec);
   }

   public static Schema ofTypes(final List types, final Codec codec) {
      return new Schema(Optional.empty(), types, Optional.empty(), Map.of(), List.of(), codec);
   }

   public static Schema ofEnum(final Supplier values) {
      return ofEnum(values, StringRepresentable.fromEnum(values));
   }

   public static Schema ofEnum(final Supplier values, final Codec codec) {
      List enumValues = Stream.of((Enum[])values.get()).map((rec$) -> ((StringRepresentable)rec$).getSerializedName()).toList();
      return ofEnum(enumValues, codec);
   }

   public static Schema ofEnum(final List enumValues, final Codec codec) {
      return new Schema(Optional.empty(), List.of("string"), Optional.empty(), Map.of(), enumValues, codec);
   }

   public static Schema arrayOf(final Schema item, final Codec codec) {
      return new Schema(Optional.empty(), List.of("array"), Optional.of(item), Map.of(), List.of(), codec.listOf());
   }

   public static Schema record(final Codec codec) {
      return new Schema(Optional.empty(), List.of("object"), Optional.empty(), Map.of(), List.of(), codec);
   }

   private static Schema record(final Map properties, final Codec codec) {
      return new Schema(Optional.empty(), List.of("object"), Optional.empty(), properties, List.of(), codec);
   }

   public Schema withField(final String name, final Schema field) {
      HashMap properties = new HashMap(this.properties);
      properties.put(name, field);
      return record(properties, this.codec);
   }

   public Schema asArray() {
      return arrayOf(this, this.codec);
   }
}

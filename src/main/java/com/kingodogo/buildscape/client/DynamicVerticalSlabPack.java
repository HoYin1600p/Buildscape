package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class DynamicVerticalSlabPack implements PackResources {
    private final Set<String> namespaces = Collections.singleton(BuildScape.MODID);

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        if (fileName.equals("pack.mcmeta")) {
            return new ByteArrayInputStream("{\"pack\":{\"description\":\"BuildScape Dynamic Assets\",\"pack_format\":8}}".getBytes(StandardCharsets.UTF_8));
        }
        throw new IOException("File not found: " + fileName);
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        if (type == PackType.CLIENT_RESOURCES && location.getNamespace().equals(BuildScape.MODID)) {
            String path = location.getPath();

            if (path.startsWith("blockstates/vertical_") && path.endsWith(".json")) {
                String blockName = path.substring("blockstates/".length(), path.length() - ".json".length());
                Block vBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BuildScape.MODID, blockName));
                if (vBlock instanceof VerticalSlabBlock vsb) {
                    return generateBlockstate(vsb);
                }
            }

            if (path.startsWith("models/block/vertical_") && path.endsWith(".json")) {
                return generateDummyModel();
            }

            if (path.startsWith("models/item/vertical_") && path.endsWith(".json")) {
                String itemName = path.substring("models/item/".length(), path.length() - ".json".length());
                String json = "{\"parent\":\"buildscape:block/" + itemName + "\"}";
                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            }
        }
        throw new IOException("File not found: " + location);
    }

    private InputStream generateBlockstate(VerticalSlabBlock block) {
        String name = block.getRegistryName().getPath();
        // Fully explicit variants to avoid any mismatch with Minecraft's default property states
        String json = "{\n" +
                "  \"variants\": {\n" +
                "    \"facing=north,type=single,waterlogged=false\": { \"model\": \"buildscape:block/" + name + "\", \"y\": 0 },\n" +
                "    \"facing=east,type=single,waterlogged=false\":  { \"model\": \"buildscape:block/" + name + "\", \"y\": 90 },\n" +
                "    \"facing=south,type=single,waterlogged=false\": { \"model\": \"buildscape:block/" + name + "\", \"y\": 180 },\n" +
                "    \"facing=west,type=single,waterlogged=false\":  { \"model\": \"buildscape:block/" + name + "\", \"y\": 270 },\n" +
                "    \"facing=north,type=single,waterlogged=true\":  { \"model\": \"buildscape:block/" + name + "\", \"y\": 0 },\n" +
                "    \"facing=east,type=single,waterlogged=true\":   { \"model\": \"buildscape:block/" + name + "\", \"y\": 90 },\n" +
                "    \"facing=south,type=single,waterlogged=true\":  { \"model\": \"buildscape:block/" + name + "\", \"y\": 180 },\n" +
                "    \"facing=west,type=single,waterlogged=true\":   { \"model\": \"buildscape:block/" + name + "\", \"y\": 270 },\n" +
                "    \"facing=north,type=double,waterlogged=false\": { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=east,type=double,waterlogged=false\":  { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=south,type=double,waterlogged=false\": { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=west,type=double,waterlogged=false\":  { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=north,type=double,waterlogged=true\":  { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=east,type=double,waterlogged=true\":   { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=south,type=double,waterlogged=true\":  { \"model\": \"buildscape:block/" + name + "_double\" },\n" +
                "    \"facing=west,type=double,waterlogged=true\":   { \"model\": \"buildscape:block/" + name + "_double\" }\n" +
                "  }\n" +
                "}";
        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream generateDummyModel() {
        String json = "{\"parent\":\"block/block\",\"textures\":{\"particle\":\"minecraft:block/oak_planks\",\"all\":\"minecraft:block/oak_planks\"}}";
        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES || !location.getNamespace().equals(BuildScape.MODID)) return false;
        String path = location.getPath();
        return path.startsWith("blockstates/vertical_") || path.startsWith("models/block/vertical_") || path.startsWith("models/item/vertical_");
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return namespaces;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return "BuildScape Dynamic Vertical Slabs";
    }

    @Override
    public void close() {
    }
}

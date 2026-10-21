package com.kingodogo.buildscape.resources.bundle.adapter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class TagAdapter {

    private TagAdapter() {}

    public static void applyTags(String directory, Map<ResourceLocation, Tag.Builder> tagMap) {
        applyTags(directory, tagMap, null);
    }

    public static void applyTags(String directory, Map<ResourceLocation, Tag.Builder> tagMap,
            ResourceManager resourceManager) {
        String registry = directory.replace('\\', '/');
        if (registry.startsWith("tags/")) {
            registry = registry.substring(5);
        }

        List<PackResources> packs = resourceManager != null ? resourceManager.listPacks().toList() : List.of();
        int bundlePackIndex = ResourcePackPrecedenceTracker.findServerBundlePackIndex(packs);
        if (bundlePackIndex < 0) {
            bundlePackIndex = ResourcePackPrecedenceTracker.findBundlePackIndex(packs);
        }

        if (resourceManager != null && bundlePackIndex < 0) {
            // An inactive or missing bundle-owning pack must not contribute tags
            return;
        }

        for (TagIR tag : BundledResourceRegistry.getInstance().getTags()) {
            if (!isMatchingRegistry(tag.registry(), registry)) {
                continue;
            }

            ResourceLocation tagLoc = new ResourceLocation(tag.id());
            ResourceLocation fileLoc = new ResourceLocation(tagLoc.getNamespace(),
                    "tags/" + registry + "/" + tagLoc.getPath() + ".json");

            if (!packs.isEmpty() && bundlePackIndex >= 0) {
                Tag.Builder builder = Tag.Builder.tag();
                boolean tagHandled = false;

                for (int i = 0; i < packs.size(); i++) {
                    PackResources pack = packs.get(i);
                    if (i == bundlePackIndex) {
                        if (tag.replace()) {
                            builder = Tag.Builder.tag();
                        }
                        addBundleEntries(builder, tag, pack.getName());
                        tagHandled = true;
                    } else {
                        try {
                            if (pack.hasResource(PackType.SERVER_DATA, fileLoc)) {
                                try (InputStream in = pack.getResource(PackType.SERVER_DATA, fileLoc);
                                        Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                                    JsonObject json = JsonParser.parseReader(r).getAsJsonObject();
                                    builder.addFromJson(json, pack.getName());
                                    tagHandled = true;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (tagHandled) {
                    tagMap.put(tagLoc, builder);
                    BundledResourceRegistry.getInstance().recordServedTag(registry, tag.id());
                }
            } else {
                // Fallback when ResourceManager is absent or has no bundle pack (e.g. standalone test)
                Tag.Builder existingBuilder = tagMap.get(tagLoc);
                Tag.Builder newBuilder = Tag.Builder.tag();
                if (existingBuilder != null && !tag.replace()) {
                    for (Tag.BuilderEntry entry : existingBuilder.getEntries().toList()) {
                        newBuilder.add(entry);
                    }
                }
                addBundleEntries(newBuilder, tag, "buildscape:bundle");
                tagMap.put(tagLoc, newBuilder);
                BundledResourceRegistry.getInstance().recordServedTag(registry, tag.id());
            }
        }
    }

    private static void addBundleEntries(Tag.Builder builder, TagIR tag, String source) {
        for (TagIR.TagEntryIR entry : tag.entries()) {
            ResourceLocation entryLoc = new ResourceLocation(entry.id());
            if (entry.tag()) {
                if (entry.required()) {
                    builder.addTag(entryLoc, source);
                } else {
                    builder.addOptionalTag(entryLoc, source);
                }
            } else {
                if (entry.required()) {
                    builder.addElement(entryLoc, source);
                } else {
                    builder.addOptionalElement(entryLoc, source);
                }
            }
        }
    }

    private static boolean isMatchingRegistry(String a, String b) {
        if (a.equals(b)) {
            return true;
        }
        if (a.equals("blocks") && b.equals("block")) {
            return true;
        }
        if (a.equals("block") && b.equals("blocks")) {
            return true;
        }
        if (a.equals("items") && b.equals("item")) {
            return true;
        }
        if (a.equals("item") && b.equals("items")) {
            return true;
        }
        return false;
    }
}

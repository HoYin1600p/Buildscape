package com.kingodogo.buildscape.client.guidebook.data;

import com.google.gson.*;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Collection;

/**
 * Loads all guidebook category JSON files from
 * {@code assets/buildscape/guidebook/categories/} and populates {@link GuideRegistry}.
 *
 * Registered as a client resource reload listener so the book automatically
 * updates when resource-packs change (F3+T).
 */
public final class GuidePageLoader
        extends SimplePreparableReloadListener<List<GuideCategory>> {

    public static final GuidePageLoader INSTANCE = new GuidePageLoader();
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String FOLDER = "guidebook/categories";

    private GuidePageLoader() {}

    // -------------------------------------------------------------------------
    // SimplePreparableReloadListener
    // -------------------------------------------------------------------------

    @Override
    protected List<GuideCategory> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<GuideCategory> result = new ArrayList<>();

        Collection<ResourceLocation> resources =
                manager.listResources(FOLDER, path -> path.endsWith(".json"));

        for (ResourceLocation rl : resources) {
            Resource resource;
            try {
                resource = manager.getResource(rl);
            } catch (IOException ex) {
                BuildScape.LOGGER.error("[BuildScape Guidebook] Resource not found: {}", rl);
                continue;
            }
            try (InputStream stream = resource.getInputStream();
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                GuideCategory category = parseCategory(json);
                if (category != null) result.add(category);
            } catch (Exception ex) {
                BuildScape.LOGGER.error("[BuildScape Guidebook] Failed to load {}: {}",
                        rl, ex.getMessage());
            }
        }

        return result;
    }

    @Override
    protected void apply(List<GuideCategory> categories, ResourceManager manager, ProfilerFiller profiler) {
        GuideRegistry.getInstance().reload(categories);
        BuildScape.LOGGER.info("[BuildScape Guidebook] Loaded {} categories.", categories.size());
    }

    // -------------------------------------------------------------------------
    // JSON parsing
    // -------------------------------------------------------------------------

    private static GuideCategory parseCategory(JsonObject json) {
        String id          = json.get("id").getAsString();
        String displayName = json.get("displayName").getAsString();
        String iconItem    = json.has("icon") ? json.get("icon").getAsString() : null;
        int    sortOrder   = json.has("sort") ? json.get("sort").getAsInt() : 100;

        List<GuideEntry> entries = new ArrayList<>();
        if (json.has("entries")) {
            for (JsonElement el : json.getAsJsonArray("entries")) {
                GuideEntry entry = parseEntry(el.getAsJsonObject());
                if (entry != null) entries.add(entry);
            }
        }

        return new GuideCategory(id, displayName, iconItem, sortOrder, entries);
    }

    private static GuideEntry parseEntry(JsonObject json) {
        String id       = json.get("id").getAsString();
        String titleKey = json.get("title").getAsString();

        List<GuidePage> pages = new ArrayList<>();
        if (json.has("pages")) {
            for (JsonElement el : json.getAsJsonArray("pages")) {
                GuidePage page = parsePage(el.getAsJsonObject());
                if (page != null) pages.add(page);
            }
        }

        // Always ensure at least one page exists so the GUI never crashes
        if (pages.isEmpty()) pages.add(new GuidePage(List.of()));
        return new GuideEntry(id, titleKey, pages);
    }

    private static GuidePage parsePage(JsonObject json) {
        // A page is a list of components; top-level object may itself be a component OR
        // may contain a "components" array.
        List<PageComponent> comps = new ArrayList<>();

        if (json.has("components")) {
            for (JsonElement el : json.getAsJsonArray("components")) {
                PageComponent comp = parseComponent(el.getAsJsonObject());
                if (comp != null) comps.add(comp);
            }
        } else {
            // Shorthand: page object IS a single component
            PageComponent comp = parseComponent(json);
            if (comp != null) comps.add(comp);
        }

        return new GuidePage(comps);
    }

    private static PageComponent parseComponent(JsonObject json) {
        if (!json.has("type")) return null;
        String type = json.get("type").getAsString();
        return switch (type) {
            case "header" -> new PageComponent.HeaderComponent(json.get("text").getAsString());
            case "text"   -> new PageComponent.TextComponent(json.get("text").getAsString());
            case "image"  -> {
                int w = json.has("width") ? json.get("width").getAsInt() : 160;
                yield new PageComponent.ImageComponent(json.get("texture").getAsString(), w);
            }
            case "item"   -> {
                String caption = json.has("caption") ? json.get("caption").getAsString() : null;
                yield new PageComponent.ItemDisplayComponent(json.get("item").getAsString(), caption);
            }
            case "spacer" -> {
                int px = json.has("pixels") ? json.get("pixels").getAsInt() : 8;
                yield new PageComponent.SpacerComponent(px);
            }
            default -> {
                BuildScape.LOGGER.error("[BuildScape Guidebook] Unknown component type: {}", type);
                yield null;
            }
        };
    }
}

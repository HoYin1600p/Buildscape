package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VerticalConfig {
    private static VerticalConfig INSTANCE;
    private final List<String> allowedFamilies = new ArrayList<>();
    private final List<String> blocklistedFamilies = new ArrayList<>();
    private final List<String> explicitlyGreyFamilies = new ArrayList<>();
    private final List<String> allowedMods = new ArrayList<>();
    private final List<String> blocklistedMods = new ArrayList<>();

    private VerticalConfig() {
        load();
    }

    public static VerticalConfig get() {
        if (INSTANCE == null) INSTANCE = new VerticalConfig();
        return INSTANCE;
    }

    private File getFile() {
        File dir = FMLPaths.CONFIGDIR.get().resolve("buildscape").toFile();
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "vertical-config.dat");
    }

    public void load() {
        File file = getFile();
        if (!file.exists()) return;
        try {
            CompoundTag nbt = NbtIo.readCompressed(file);
            allowedFamilies.clear();
            blocklistedFamilies.clear();
            explicitlyGreyFamilies.clear();
            allowedMods.clear();
            blocklistedMods.clear();

            if (nbt.contains("allowedFamilies")) {
                ListTag list = nbt.getList("allowedFamilies", 8);
                for (int i = 0; i < list.size(); i++) allowedFamilies.add(list.getString(i));
            }
            if (nbt.contains("blocklistedFamilies")) {
                ListTag list = nbt.getList("blocklistedFamilies", 8);
                for (int i = 0; i < list.size(); i++) blocklistedFamilies.add(list.getString(i));
            }
            if (nbt.contains("explicitlyGreyFamilies")) {
                ListTag list = nbt.getList("explicitlyGreyFamilies", 8);
                for (int i = 0; i < list.size(); i++) explicitlyGreyFamilies.add(list.getString(i));
            }
            if (nbt.contains("allowedMods")) {
                ListTag list = nbt.getList("allowedMods", 8);
                for (int i = 0; i < list.size(); i++) allowedMods.add(list.getString(i));
            }
            if (nbt.contains("blocklistedMods")) {
                ListTag list = nbt.getList("blocklistedMods", 8);
                for (int i = 0; i < list.size(); i++) blocklistedMods.add(list.getString(i));
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to load vertical config", e);
        }
    }

    public void save() {
        File file = getFile();
        CompoundTag nbt = new CompoundTag();
        
        ListTag allowedFamTag = new ListTag();
        for (String s : allowedFamilies) allowedFamTag.add(StringTag.valueOf(s));
        nbt.put("allowedFamilies", allowedFamTag);

        ListTag blockFamTag = new ListTag();
        for (String s : blocklistedFamilies) blockFamTag.add(StringTag.valueOf(s));
        nbt.put("blocklistedFamilies", blockFamTag);

        ListTag greyFamTag = new ListTag();
        for (String s : explicitlyGreyFamilies) greyFamTag.add(StringTag.valueOf(s));
        nbt.put("explicitlyGreyFamilies", greyFamTag);

        ListTag allowedModTag = new ListTag();
        for (String s : allowedMods) allowedModTag.add(StringTag.valueOf(s));
        nbt.put("allowedMods", allowedModTag);

        ListTag blockModTag = new ListTag();
        for (String s : blocklistedMods) blockModTag.add(StringTag.valueOf(s));
        nbt.put("blocklistedMods", blockModTag);

        try {
            NbtIo.writeCompressed(nbt, file);
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to save vertical config", e);
        }
    }

    public List<String> getAllowedFamilies() { return allowedFamilies; }
    public List<String> getBlocklistedFamilies() { return blocklistedFamilies; }
    public List<String> getExplicitlyGreyFamilies() { return explicitlyGreyFamilies; }
    public List<String> getAllowedMods() { return allowedMods; }
    public List<String> getBlocklistedMods() { return blocklistedMods; }
    
    public boolean isModAllowed(String namespace) {
        return allowedMods.contains(namespace);
    }

    public boolean isModBlocklisted(String namespace) {
        return blocklistedMods.contains(namespace);
    }

    public boolean isFamilyAllowed(String coreName, String namespace) {
        if (!allowedMods.isEmpty() && !allowedMods.contains(namespace)) return false;
        if (blocklistedMods.contains(namespace)) return false;
        
        if (!allowedFamilies.isEmpty() && !allowedFamilies.contains(coreName)) return false;
        return !blocklistedFamilies.contains(coreName);
    }
}

package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MobStateParser {

    private static final Map<String, Set<String>> validMobStates = new HashMap<>();
    private static final Set<String> universalStates = new HashSet<>();
    private static boolean statesLoaded = false;

    public static void loadStates() {
        if (statesLoaded) {
            return;
        }

        try {
            addUniversalStates();

            java.io.File rootStatesFile = new java.io.File("states.txt");
            InputStream stream = null;

            if (rootStatesFile.exists()) {
                stream = new java.io.FileInputStream(rootStatesFile);
            } else {
                ResourceLocation statesFile = new ResourceLocation("buildscape:mob_states.txt");
                try {
                    stream = Minecraft.getInstance()
                            .getResourceManager()
                            .getResource(statesFile)
                            .getInputStream();
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Could not find mob_states.txt resource.");
                }
            }

            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                String currentMob = null;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty()) {
                        continue;
                    }

                    if (line.contains("|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            String mobName = parts[0].trim().toLowerCase().replace(" ", "_");
                            String stateName = parts[1].trim().toLowerCase();

                            if (stateName.contains(" ")) {
                                String[] stateWords = stateName.split("\\s+");
                                for (String s : stateWords) {
                                    addStateToMob(mobName, s);
                                }
                            } else {
                                addStateToMob(mobName, stateName);
                            }

                            if (mobName.equals("*")) {
                                universalStates.add(stateName);
                            }
                        }
                        continue;
                    }

                    if (line.startsWith("**") && line.endsWith("**")) {
                        currentMob = line.substring(2, line.length() - 2).trim().toLowerCase();
                        currentMob = currentMob.replace(" ", "_");
                        continue;
                    }

                    if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                        if (currentMob != null) {
                            String stateName = line.substring(1).trim().toLowerCase();
                            if (stateName.endsWith(":")) {
                                continue;
                            }
                            addStateToMob(currentMob, stateName);
                        }
                    }
                }

                reader.close();
            }

            statesLoaded = true;




        } catch (Exception e) {
            System.err.println("[BuildScape] Failed to load states: " + e.getMessage());
            e.printStackTrace();
            addUniversalStates();
            statesLoaded = true;
        }
    }

    private static void addUniversalStates() {
        universalStates.add("spin");
        universalStates.add("dinnerbone");
        universalStates.add("grum");
        universalStates.add("glowing");
        universalStates.add("fire");
        universalStates.add("frozen");
        universalStates.add("invisible");
        universalStates.add("hurt");
        universalStates.add("damage");

        universalStates.add("hanging");
        universalStates.add("roosting");
        universalStates.add("standing");
        universalStates.add("rearing");
        universalStates.add("screaming");
        universalStates.add("staring");
        universalStates.add("climbing");
        universalStates.add("begging");
        universalStates.add("charging");

        universalStates.add("spruce");
        universalStates.add("birch");
        universalStates.add("jungle");
        universalStates.add("acacia");
        universalStates.add("dark_oak");
        universalStates.add("darkoak");
        universalStates.add("mangrove");

        universalStates.add("arms");
        universalStates.add("no_base");
        universalStates.add("nobase");
        universalStates.add("no_bottom");
        universalStates.add("nobottom");

        universalStates.add("baby");
        universalStates.add("saddled");
        universalStates.add("sheared");
        universalStates.add("sitting");

        universalStates.add("white");
        universalStates.add("orange");
        universalStates.add("magenta");
        universalStates.add("light_blue");
        universalStates.add("yellow");
        universalStates.add("lime");
        universalStates.add("pink");
        universalStates.add("gray");
        universalStates.add("light_gray");
        universalStates.add("cyan");
        universalStates.add("purple");
        universalStates.add("blue");
        universalStates.add("brown");
        universalStates.add("green");
        universalStates.add("red");
        universalStates.add("black");

        universalStates.add("tamed");
        universalStates.add("angry");
        universalStates.add("charged");

        universalStates.add("giant");
        universalStates.add("huge");
        universalStates.add("large");
        universalStates.add("medium");
        universalStates.add("small");
        universalStates.add("tiny");

        universalStates.add("shield");
        universalStates.add("invul");
        universalStates.add("pumpkin");
        universalStates.add("nopumpkin");
        universalStates.add("cracked");
        universalStates.add("broken");
        universalStates.add("puff");
        universalStates.add("full");
        universalStates.add("half");
        universalStates.add("chest");
        universalStates.add("chested");

        universalStates.add("rainbow");
        universalStates.add("jeb");
        universalStates.add("johnny");
        universalStates.add("cold");
        universalStates.add("shivering");
        universalStates.add("warm");
        universalStates.add("temperate");
        universalStates.add("block");
        universalStates.add("carrying");
        universalStates.add("casting");
        universalStates.add("spell");

        universalStates.add("diamond");
        universalStates.add("gold");
        universalStates.add("iron");
        universalStates.add("leather");
        universalStates.add("armor");
    }

    private static void addStateToMob(String mobName, String stateName) {
        validMobStates.computeIfAbsent(mobName, k -> new HashSet<>()).add(stateName);
    }

    public static MobState parseStates(ItemStack spawnEggStack, EntityType<?> entityType) {
        MobState state = new MobState();

        if (spawnEggStack == null || spawnEggStack.isEmpty()) {
            return state;
        }

        String customName = getCustomName(spawnEggStack);
        if (customName == null || customName.isEmpty()) {
            return state;
        }

        loadStates();

        String mobTypeName = entityType.getDescriptionId().toLowerCase();
        if (mobTypeName.contains(".")) {
            String[] parts = mobTypeName.split("\\.");
            mobTypeName = parts[parts.length - 1];
        }

        String lowerName = customName.toLowerCase();

        lowerName = lowerName.replace("no pumpkin", "nopumpkin");
        lowerName = lowerName.replace("no horns", "nohorns");
        lowerName = lowerName.replace("no ai", "noai");

        String[] words = lowerName.split("\\s+");

        for (String word : words) {
            word = resolveAlias(word.trim());
            if (word.isEmpty()) {
                continue;
            }

            state.parsedStates.add(word);


            if (word.equals("spin")) {
                state.spin = true;
            } else if (word.equals("dinnerbone") || word.equals("grum")) {
                state.upsideDown = true;
            } else if (word.equals("baby")) {
                state.baby = true;
            } else if (word.equals("angry") || word.equals("aggro") || word.equals("johnny")) {
                state.angry = true;
            } else if (word.equals("sitting") || word.equals("sit")) {
                state.sitting = true;
            } else if (word.equals("charged") || word.equals("charge")) {
                state.charged = true;
            } else if (word.equals("sheared") || word.equals("shear") || word.equals("nopumpkin")) {
                state.sheared = true;
            } else if (word.equals("saddled") || word.equals("saddle")) {
                state.saddled = true;
            } else if (word.equals("tamed") || word.equals("tame")) {
                state.tamed = true;
            } else if (word.equals("powered") || word.equals("power")) {
                state.powered = true;
            } else if (word.equals("invisible")) {
                state.invisible = true;
            } else if (word.equals("glowing")) {
                state.glowing = true;
            } else if (word.equals("fire")) {
                state.fire = true;
            } else if (word.equals("frozen") || word.equals("shivering") || word.equals("cold")) {
                state.frozen = true;
            }
        }

        return state;
    }

    private static String resolveAlias(String word) {
        if (word.equals("sit")) return "sitting";
        if (word.equals("tame")) return "tamed";
        if (word.equals("power")) return "powered";
        if (word.equals("charge")) return "charged";
        if (word.equals("saddle")) return "saddled";
        if (word.equals("shear")) return "sheared";
        if (word.equals("ignite")) return "ignited";
        if (word.equals("sleep")) return "sleeping";
        if (word.equals("crouch")) return "crouching";
        if (word.equals("scream")) return "screaming";
        if (word.equals("aggro")) return "angry";
        if (word.equals("grumm")) return "grum";
        if (word.equals("jeb_")) return "jeb";

        if (word.equals("beg")) return "begging";
        if (word.equals("stand")) return "standing";
        if (word.equals("chest")) return "chested";
        if (word.equals("glow")) return "glowing";
        if (word.equals("freeze")) return "frozen";
        if (word.equals("hang")) return "hanging";
        if (word.equals("roost")) return "roosting";
        if (word.equals("climb")) return "climbing";
        if (word.equals("cast")) return "casting";
        if (word.equals("carry")) return "carrying";
        if (word.equals("rear")) return "rearing";
        if (word.equals("stare")) return "staring";
        if (word.equals("crack")) return "cracked";
        if (word.equals("break")) return "broken";
        if (word.equals("damage")) return "hurt";
        if (word.equals("puffed")) return "puff";
        if (word.equals("opened")) return "open";
        if (word.equals("invulnerable")) return "invul";

        if (word.equals("no_pumpkin")) return "nopumpkin";
        if (word.equals("no_horns")) return "nohorns";
        if (word.equals("no_base")) return "nobase";
        if (word.equals("no_bottom")) return "nobottom";

        return word;
    }

    private static String getCustomName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("display", 10)) {
            return null;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) {
            return null;
        }

        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return null;
        }

        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent != null) {
                String displayName = nameComponent.getString();
                if (displayName != null && !displayName.isEmpty()) {
                    return net.minecraft.ChatFormatting.stripFormatting(displayName.trim());
                }
            }
        } catch (Exception e) {
            return net.minecraft.ChatFormatting.stripFormatting(nameJson.trim());
        }

        return null;
    }

    public static void reloadStates() {
        statesLoaded = false;
        validMobStates.clear();
        universalStates.clear();
        loadStates();
    }
}

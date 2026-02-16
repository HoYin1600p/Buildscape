package com.kingodogo.buildscape.client.renderer;

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

    // Parsed state definitions from mob_states.txt or states.txt
    private static final Map<String, Set<String>> validMobStates = new HashMap<>();
    private static final Set<String> universalStates = new HashSet<>();
    private static boolean statesLoaded = false;

    /**
     * Load and parse the states configuration file.
     * Supports both formats:
     * 1. **MobName** header with bullet points
     * 2. mob_name|state_name|description pipe-delimited
     */
    public static void loadStates() {
        if (statesLoaded) {
            return;
        }

        try {
            // Add universal states first (always available)
            addUniversalStates();

            // Try to load from root directory first (user's custom file)
            java.io.File rootStatesFile = new java.io.File("states.txt");
            InputStream stream = null;

            if (rootStatesFile.exists()) {
                stream = new java.io.FileInputStream(rootStatesFile);
                System.out.println("[BuildScape] Loading states from root directory: states.txt");
            } else {
                // Fallback to resource file
                ResourceLocation statesFile = new ResourceLocation("buildscape:mob_states.txt");
                try {
                    stream = Minecraft.getInstance()
                            .getResourceManager()
                            .getResource(statesFile)
                            .getInputStream();
                    System.out.println("[BuildScape] Loading states from resources: mob_states.txt");
                } catch (Exception e) {
                    System.err.println("[BuildScape] Could not find mob_states.txt resource.");
                }
            }

            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                String currentMob = null;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    // Skip empty lines
                    if (line.isEmpty()) {
                        continue;
                    }

                    // Format 2: mob|state|desc (Pipe-delimited)
                    if (line.contains("|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            String mobName = parts[0].trim().toLowerCase().replace(" ", "_");
                            String stateName = parts[1].trim().toLowerCase();

                            // Handle combined states (e.g. "baby spin") by taking the first word or checking known states
                            // For simplicity, we add the full string and individual words if separated
                            if (stateName.contains(" ")) {
                                String[] stateWords = stateName.split("\\s+");
                                for (String s : stateWords) {
                                    addStateToMob(mobName, s);
                                }
                            } else {
                                addStateToMob(mobName, stateName);
                            }

                            // Also handle wildcard mob "*"
                            if (mobName.equals("*")) {
                                universalStates.add(stateName);
                            }
                        }
                        continue;
                    }

                    // Format 1: **MobName** header
                    if (line.startsWith("**") && line.endsWith("**")) {
                        currentMob = line.substring(2, line.length() - 2).trim().toLowerCase();
                        // Normalize mob names (remove spaces, handle special cases)
                        currentMob = currentMob.replace(" ", "_");
                        continue;
                    }

                    // Format 1: • stateName bullet point
                    if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                        if (currentMob != null) {
                            String stateName = line.substring(1).trim().toLowerCase();
                            // Skip section headers like "Biomes:" or "Professions:"
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

            System.out.println("[BuildScape] Loaded " + validMobStates.size() + " mob state definitions");
            System.out.println("[BuildScape] Universal states: " + universalStates);

        } catch (Exception e) {
            System.err.println("[BuildScape] Failed to load states: " + e.getMessage());
            e.printStackTrace();
            // Ensure at least universal states are loaded
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

        // Entity Specific States
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
        universalStates.add("mangrove"); // 1.19+ but harmless

        // Armor Stand / End Crystal Utils
        universalStates.add("arms");
        universalStates.add("no_base");
        universalStates.add("nobase");
        universalStates.add("no_bottom");
        universalStates.add("nobottom");

        // Add common states that should universally apply (especially for modded support)
        universalStates.add("baby");
        universalStates.add("saddled");
        universalStates.add("sheared");
        universalStates.add("sitting");

        // Add all dye colors as universal states (for wool, collars, shulkers, horses, rabbits, etc.)
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

        // Size variants 
        universalStates.add("giant");
        universalStates.add("huge");
        universalStates.add("large"); // Size 4
        universalStates.add("medium"); // Size 2
        universalStates.add("small");
        universalStates.add("tiny"); // Size 1

        // Specific vanilla variants
        universalStates.add("shield"); // Wither
        universalStates.add("invul");
        universalStates.add("pumpkin"); // Snow Golem
        universalStates.add("nopumpkin");
        universalStates.add("cracked"); // Iron Golem
        universalStates.add("broken");
        universalStates.add("puff"); // Pufferfish
        universalStates.add("full");
        universalStates.add("half");
        universalStates.add("chest"); // Horse/Llama chest
        universalStates.add("chested");

        // New Additions
        universalStates.add("rainbow"); // Sheep jeb_
        universalStates.add("jeb");
        universalStates.add("johnny"); // Vindicator
        universalStates.add("cold"); // Strider / Frog
        universalStates.add("shivering");
        universalStates.add("warm"); // Frog
        universalStates.add("temperate");
        universalStates.add("block"); // Enderman
        universalStates.add("carrying");
        universalStates.add("casting"); // Evoker / Illusioner
        universalStates.add("spell");

        // Horse Armor
        universalStates.add("diamond");
        universalStates.add("gold");
        universalStates.add("iron");
        universalStates.add("leather");
        universalStates.add("armor"); // Generic armor keyword
    }

    private static void addStateToMob(String mobName, String stateName) {
        validMobStates.computeIfAbsent(mobName, k -> new HashSet<>()).add(stateName);
    }

    /**
     * Parse the spawn egg's custom name to extract mob states
     */
    public static MobState parseStates(ItemStack spawnEggStack, EntityType<?> entityType) {
        MobState state = new MobState();

        if (spawnEggStack == null || spawnEggStack.isEmpty()) {
            return state;
        }

        // Get the custom name from NBT
        String customName = getCustomName(spawnEggStack);
        if (customName == null || customName.isEmpty()) {
            return state;
        }

        // Ensure states are loaded
        loadStates();

        // Get mob type name
        String mobTypeName = entityType.getDescriptionId().toLowerCase();
        if (mobTypeName.contains(".")) {
            String[] parts = mobTypeName.split("\\.");
            mobTypeName = parts[parts.length - 1];
        }

        // Parse the custom name for state keywords
        String lowerName = customName.toLowerCase();

        // Pre-processing for combined keywords (e.g. "no pumpkin" -> "nopumpkin")
        lowerName = lowerName.replace("no pumpkin", "nopumpkin");
        lowerName = lowerName.replace("no horns", "nohorns");
        lowerName = lowerName.replace("no ai", "noai");

        String[] words = lowerName.split("\\s+");

        // Check each word against valid states
        for (String word : words) {
            word = resolveAlias(word.trim());
            if (word.isEmpty()) {
                continue;
            }

            // Add to parsed states for variant checking
            state.parsedStates.add(word);

            // ... (Optimization: Skip isValidState check if we are just parsing broadly)

            // Apply state based on keyword
            // We use parsedStates set for most things now, but keep bools for common ones
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

    /**
     * Resolve aliases for state names (e.g., "sit" -> "sitting")
     */
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

        // Comprehensive aliases for relatability
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

        // Strict mapping for 'no' prefix
        if (word.equals("no_pumpkin")) return "nopumpkin";
        if (word.equals("no_horns")) return "nohorns";
        if (word.equals("no_base")) return "nobase";
        if (word.equals("no_bottom")) return "nobottom";

        return word;
    }

    /**
     * Extract custom name from spawn egg ItemStack
     */
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
            // Fallback to raw string
            return net.minecraft.ChatFormatting.stripFormatting(nameJson.trim());
        }

        return null;
    }

    // Explicitly expose clear cache methods if needed, essentially just reloading states
    public static void reloadStates() {
        statesLoaded = false;
        validMobStates.clear();
        universalStates.clear();
        loadStates();
    }
}

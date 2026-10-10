"""Block entity IDs verified against Buildscape's registrations and 26.2 types."""

# Keys include the official block class, adapter class and definition block type.
BUILD_SCAPE = {
    "BuildersWorkbench": "builders_workbench", "Shelf": "shelf",
    "GlassJar": "glass_jar_block_entity", "SmokeVent": "smoke_vent_block_entity",
    "Muff": "muff_block_entity", "CopperChest": "copper_chest",
    "DecoratedPot": "decorated_pot_block_entity",
    "TrappedDecoratedPot": "trapped_decorated_pot_block_entity",
    "FestiveStocking": "festive_stocking_block_entity",
    "Cascade": "cascade_block_entity", "CascadeBlockNoMist": "cascade_block_entity",
    "IcicleCauldron": "icicle_cauldron_block_entity", "PotentSulfur": "potent_sulfur",
    "Trophy": "trophy_block_entity", "Pillar": "pillar_block_entity",
    "AshenKingPillar": "pillar_block_entity", "GlowLights": "glow_lights_block_entity",
    "HollowLog": "hollow_log", "HollowPipe": "hollow_log",
    "V26xBambooStandingSign": "bamboo_sign_block_entity",
    "V26xBambooWallSign": "bamboo_sign_block_entity",
    "V26xMangroveStandingSign": "mangrove_sign_block_entity",
    "V26xMangroveWallSign": "mangrove_sign_block_entity",
}

VANILLA = {
    "Sign": "sign", "StandingSign": "sign", "WallSign": "sign",
    "CeilingHangingSign": "hanging_sign", "WallHangingSign": "hanging_sign",
    "HangingSign": "hanging_sign", "Chest": "chest", "TrappedChest": "trapped_chest",
    "EnderChest": "ender_chest", "Barrel": "barrel", "ShulkerBox": "shulker_box",
    "Bed": "bed", "StrawBed": "bed", "Banner": "banner", "WallBanner": "banner",
    "Furnace": "furnace", "BlastFurnace": "blast_furnace", "Smoker": "smoker",
    "Hopper": "hopper", "Dispenser": "dispenser", "Dropper": "dropper",
    "BrewingStand": "brewing_stand", "EnchantingTable": "enchanting_table",
    "Beacon": "beacon", "Conduit": "conduit", "Jukebox": "jukebox",
    "Lectern": "lectern", "Campfire": "campfire", "Beehive": "beehive",
    "ChiseledBookshelf": "chiseled_bookshelf", "Crafter": "crafter",
    "Skull": "skull", "WallSkull": "skull", "PlayerHead": "skull",
    "PlayerWallHead": "skull", "DecoratedPot": "decorated_pot",
}


def entity_id(block_id, class_name="", block_type=""):
    namespace, path = block_id.split(":", 1)
    names = [name.rsplit(".", 1)[-1].rsplit("$", 1)[-1] for name in (class_name, block_type)]
    names = [name[:-5] if name.endswith("Block") else name for name in names]
    if "MulticolorGlowLights" in names or "multicolor_glow_lights" in path:
        return None  # This subclass explicitly returns null from newBlockEntity.
    if namespace == "buildscape":
        for name in names:
            if name in BUILD_SCAPE:
                return "buildscape:" + BUILD_SCAPE[name]
        # Compatibility with layouts generated before class metadata was retained.
        custom = (("trapped_decorated_pot", "trapped_decorated_pot_block_entity"),
                  ("decorated_pot", "decorated_pot_block_entity"),
                  ("copper_chest", "copper_chest"), ("builders_workbench", "builders_workbench"),
                  ("icicle_cauldron", "icicle_cauldron_block_entity"),
                  ("festive_stocking", "festive_stocking_block_entity"),
                  ("glow_lights", "glow_lights_block_entity"),
                  ("smoke_vent", "smoke_vent_block_entity"), ("glass_jar", "glass_jar_block_entity"),
                  ("hollow_log", "hollow_log"), ("hollow_pipe", "hollow_log"),
                  ("potent_sulfur", "potent_sulfur"), ("cascade", "cascade_block_entity"),
                  ("muff", "muff_block_entity"), ("shelf", "shelf"),
                  ("trophy", "trophy_block_entity"), ("pillar", "pillar_block_entity"))
        for token, result in custom:
            if path == token or path.endswith("_" + token) or path.startswith(token + "_"):
                return "buildscape:" + result
        if path in ("bamboo_sign", "bamboo_wall_sign", "mangrove_sign", "mangrove_wall_sign"):
            return "buildscape:" + path.split("_", 1)[0] + "_sign_block_entity"
    for name in names:
        if name in VANILLA:
            return "minecraft:" + VANILLA[name]
    for suffix in ("hanging_sign", "sign", "trapped_chest", "ender_chest", "chest",
                   "shulker_box", "barrel", "bed", "banner"):
        if path == suffix or path.endswith("_" + suffix):
            return "minecraft:" + suffix
    if namespace == "minecraft" and path in set(VANILLA.values()):
        return "minecraft:" + path
    return None

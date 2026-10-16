package com.kingodogo.buildscape.config;

import java.util.Set;

final class VaultPillarItems {
    private VaultPillarItems() { }

    // The Vault integration from 4.0.4; vanilla defaults remain in PillarParticleConfig.
    private static final Set<String> ITEMS = Set.of(
            "the_vault:echo_pog", "the_vault:gem_pog", "the_vault:vault_crystal",
            "the_vault:spicy_hearty_burger", "the_vault:omega_pog", "the_vault:knowledge_star",
            "the_vault:antique", "the_vault:herald_trophy", "the_vault:pvp_trophy",
            "the_vault:treasure_keyring", "the_vault:companion_egg", "the_vault:vault_artifact",
            "the_vault:tool", "the_vault:deck_socket", "the_vault:card_deck",
            "the_vault:vault_god_charm", "the_vault:boost_modification_stone", "the_vault:neuralizer",
            "the_vault:soul_vortex", "#the_vault:crystal_capstones", "#the_vault:keys",
            "#the_vault:gems", "#the_vault:fruits", "the_vault:unidentified_artifact",
            "#the_vault:playerclusters", "#the_vault:perfectgems", "#the_vault:playerchunks",
            "#the_vault:magnet", "#the_vault:unique", "#the_vault:vault_gear",
            "the_vault:santa_egg", "the_vault:grinch_egg", "the_vault:yeti_egg");

    static boolean merge(Set<String> items, boolean vaultLoaded) {
        return vaultLoaded && items.addAll(ITEMS);
    }
}

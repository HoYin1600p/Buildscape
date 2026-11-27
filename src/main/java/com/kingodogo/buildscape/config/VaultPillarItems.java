package com.kingodogo.buildscape.config;

import java.util.Set;

final class VaultPillarItems {
    private VaultPillarItems() { }

    static boolean merge(Set<String> items, boolean vaultLoaded) {
        return vaultLoaded && items.addAll(DefaultPillarItems.vaultItems());
    }
}

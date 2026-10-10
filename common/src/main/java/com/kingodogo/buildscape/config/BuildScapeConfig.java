package com.kingodogo.buildscape.config;

public class BuildScapeConfig {
    public static int getMaxPipeNetworkSize() {
        return BuildscapeClientConfig.get().getMaxPipeNetworkSize();
    }

    public static void setMaxPipeNetworkSize(int size) {
        BuildscapeClientConfig.get().setMaxPipeNetworkSize(size);
    }
}

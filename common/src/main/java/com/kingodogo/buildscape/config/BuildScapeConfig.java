package com.kingodogo.buildscape.config;

public class BuildScapeConfig {
    private static int maxPipeNetworkSize = 64;

    public static int getMaxPipeNetworkSize() {
        return maxPipeNetworkSize;
    }

    public static void setMaxPipeNetworkSize(int size) {
        maxPipeNetworkSize = size;
    }
}

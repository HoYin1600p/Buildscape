package com.kingodogo.buildscape.config;

/**
 * Small read-only access point for settings other code needs without depending on the client config class.
 */
public class BuildScapeConfig {
    public static int getMaxPipeNetworkSize() {
        return BuildscapeClientConfig.get().getMaxPipeNetworkSize();
    }
}

package com.kingodogo.buildscape.registry;

import java.util.function.Supplier;
public interface RegistrySupplier<T> extends Supplier<T> {

    /**
     * Gets the full namespaced identifier string (e.g. "buildscape:black_sand").
     */
    String getId();

    /**
     * Gets the namespace of the registered object (e.g. "buildscape").
     */
    String getNamespace();

    /**
     * Gets the path/name of the registered object (e.g. "black_sand").
     */
    String getPath();

    /**
     * Checks if the underlying object is registered and available.
     */
    boolean isPresent();
}

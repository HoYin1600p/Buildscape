package com.kingodogo.buildscape.registry;

import java.util.Objects;
import java.util.function.Supplier;
public class SimpleRegistrySupplier<T> implements RegistrySupplier<T> {
    private final String namespace;
    private final String path;
    private final Supplier<T> supplier;
    private volatile T value;

    public SimpleRegistrySupplier(String namespace, String path, Supplier<T> supplier) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.path = Objects.requireNonNull(path, "path cannot be null");
        this.supplier = Objects.requireNonNull(supplier, "supplier cannot be null");
    }

    @Override
    public String getId() {
        return namespace + ":" + path;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public T get() {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = supplier.get();
                }
            }
        }
        return value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public String toString() {
        return "RegistrySupplier[" + getId() + "]";
    }
}

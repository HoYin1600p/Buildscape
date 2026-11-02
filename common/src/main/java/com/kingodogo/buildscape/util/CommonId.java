package com.kingodogo.buildscape.util;

import java.util.Objects;
public final class CommonId implements Comparable<CommonId> {
    public static final String DEFAULT_NAMESPACE = "minecraft";
    private static final java.util.regex.Pattern NAMESPACE = java.util.regex.Pattern.compile("[a-z0-9_.-]+");
    private static final java.util.regex.Pattern PATH = java.util.regex.Pattern.compile("[a-z0-9/._-]+");
    private final String namespace;
    private final String path;

    public CommonId(String namespace, String path) {
        this.namespace = (namespace == null || namespace.isEmpty()) ? DEFAULT_NAMESPACE : namespace;
        this.path = path != null ? path : "";
    }

    public static CommonId of(String namespace, String path) {
        return new CommonId(namespace, path);
    }

    public static CommonId parse(String location) {
        if (location == null || location.isEmpty()) {
            return new CommonId(DEFAULT_NAMESPACE, "");
        }
        int idx = location.indexOf(':');
        if (idx < 0) {
            return new CommonId(DEFAULT_NAMESPACE, location);
        }
        return new CommonId(location.substring(0, idx), location.substring(idx + 1));
    }

    public static CommonId tryParse(String location) {
        if (location == null || location.isEmpty()) return null;
        int separator = location.indexOf(':');
        if (separator != location.lastIndexOf(':')) return null;
        String namespace = separator < 0 ? DEFAULT_NAMESPACE : location.substring(0, separator);
        String path = separator < 0 ? location : location.substring(separator + 1);
        if (path.isEmpty() || (!namespace.isEmpty() && !NAMESPACE.matcher(namespace).matches())
                || !PATH.matcher(path).matches()) return null;
        return new CommonId(namespace, path);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonId commonId)) return false;
        return Objects.equals(namespace, commonId.namespace) && Objects.equals(path, commonId.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public int compareTo(CommonId o) {
        int c = this.namespace.compareTo(o.namespace);
        return c != 0 ? c : this.path.compareTo(o.path);
    }
}

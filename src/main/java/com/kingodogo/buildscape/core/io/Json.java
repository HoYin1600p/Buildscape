package com.kingodogo.buildscape.core.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Shared Gson instances, one per distinct builder configuration. Gson is thread-safe, so every caller with the
 * same settings can use the same instance.
 */
public final class Json {
    /** Default settings: compact output, nulls skipped, HTML characters escaped. */
    public static final Gson PLAIN = new Gson();
    /** Pretty printed, nulls skipped, HTML characters escaped. */
    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
    /** Pretty printed with null fields written, HTML characters escaped. */
    public static final Gson PRETTY_WITH_NULLS = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    /** Pretty printed, nulls skipped, HTML characters kept as they are. */
    public static final Gson PRETTY_NO_HTML_ESCAPING = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .create();
    /** Compact output, nulls skipped, HTML characters kept as they are. */
    public static final Gson COMPACT_NO_HTML_ESCAPING = new GsonBuilder().disableHtmlEscaping().create();

    private Json() {}
}

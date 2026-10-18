package com.kingodogo.buildscape.block.copper;

import java.util.IdentityHashMap;
import java.util.Map;

public final class CopperTransitions<T> {
    private final Map<T, T> next = new IdentityHashMap<>();
    private final Map<T, T> previous = new IdentityHashMap<>();
    private final Map<T, T> first = new IdentityHashMap<>();
    private final Map<T, T> waxed = new IdentityHashMap<>();
    private final Map<T, T> unwaxed = new IdentityHashMap<>();

    public void addChain(T fresh, T exposed, T weathered, T oxidized) {
        next.put(fresh, exposed);
        next.put(exposed, weathered);
        next.put(weathered, oxidized);
        previous.put(exposed, fresh);
        previous.put(weathered, exposed);
        previous.put(oxidized, weathered);
        first.put(fresh, fresh);
        first.put(exposed, fresh);
        first.put(weathered, fresh);
        first.put(oxidized, fresh);
    }

    public void addWaxPair(T plain, T coated) {
        waxed.put(plain, coated);
        unwaxed.put(coated, plain);
    }

    public T next(T block) {
        return next.get(block);
    }

    public T previous(T block) {
        return previous.get(block);
    }

    public T first(T block) {
        return first.get(block);
    }

    public T waxed(T block) {
        return waxed.get(block);
    }

    public T unwaxed(T block) {
        return unwaxed.get(block);
    }
}

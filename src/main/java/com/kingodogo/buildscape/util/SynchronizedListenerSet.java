package com.kingodogo.buildscape.util;

import com.google.common.collect.Sets;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** HashSet whose operations are synchronized; used for vanilla's preparing-listener set during reloads. */
public final class SynchronizedListenerSet extends HashSet<PreparableReloadListener> {
    public SynchronizedListenerSet(Iterable<PreparableReloadListener> listeners) {
        super(Sets.newHashSet(listeners));
    }

    @Override
    public synchronized boolean add(PreparableReloadListener listener) {
        return super.add(listener);
    }

    @Override
    public synchronized boolean remove(Object listener) {
        return super.remove(listener);
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    public synchronized boolean contains(Object listener) {
        return super.contains(listener);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @Override
    public synchronized boolean addAll(Collection<? extends PreparableReloadListener> listeners) {
        return super.addAll(listeners);
    }

    @Override
    public synchronized boolean removeAll(Collection<?> listeners) {
        return super.removeAll(listeners);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> listeners) {
        return super.retainAll(listeners);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> listeners) {
        return super.containsAll(listeners);
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super PreparableReloadListener> filter) {
        return super.removeIf(filter);
    }

    @Override
    public synchronized void forEach(Consumer<? super PreparableReloadListener> action) {
        super.forEach(action);
    }

    @Override
    public synchronized Object[] toArray() {
        return super.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] array) {
        return super.toArray(array);
    }

    @Override
    public synchronized Iterator<PreparableReloadListener> iterator() {
        Iterator<PreparableReloadListener> delegate = super.iterator();
        Object lock = this;
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                synchronized (lock) {
                    return delegate.hasNext();
                }
            }

            @Override
            public PreparableReloadListener next() {
                synchronized (lock) {
                    return delegate.next();
                }
            }

            @Override
            public void remove() {
                synchronized (lock) {
                    delegate.remove();
                }
            }

            @Override
            public void forEachRemaining(Consumer<? super PreparableReloadListener> action) {
                synchronized (lock) {
                    delegate.forEachRemaining(action);
                }
            }
        };
    }

    @Override
    public synchronized Spliterator<PreparableReloadListener> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT);
    }

    @Override
    public synchronized Object clone() {
        return super.clone();
    }

    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }

    @Override
    public synchronized boolean equals(Object other) {
        return super.equals(other);
    }
}

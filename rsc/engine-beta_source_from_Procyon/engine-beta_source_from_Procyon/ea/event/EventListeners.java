// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import java.util.Iterator;
import java.util.function.Consumer;
import ea.internal.annotations.API;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import java.util.Collection;

public final class EventListeners<T>
{
    private final Collection<T> listeners;
    private final Collection<T> listenerIterationCopy;
    private final Collection<Runnable> pendingCopyModifications;
    private final Supplier<EventListeners<T>> parentSupplier;
    private boolean iterating;
    
    public EventListeners() {
        this(() -> null);
    }
    
    public EventListeners(final Supplier<EventListeners<T>> parentSupplier) {
        this.listeners = new LinkedHashSet<T>();
        this.listenerIterationCopy = new LinkedHashSet<T>();
        this.pendingCopyModifications = new ArrayList<Runnable>();
        this.iterating = false;
        this.parentSupplier = parentSupplier;
    }
    
    @API
    public synchronized void add(final T listener) {
        this.listeners.add(listener);
        if (this.iterating) {
            this.pendingCopyModifications.add(() -> this.listenerIterationCopy.add(listener));
        }
        else {
            this.listenerIterationCopy.add(listener);
        }
        final EventListeners<T> parent = this.parentSupplier.get();
        if (parent != null) {
            parent.add(listener);
        }
    }
    
    @API
    public synchronized void remove(final T listener) {
        this.listeners.remove(listener);
        if (this.iterating) {
            this.pendingCopyModifications.add(() -> this.listenerIterationCopy.remove(listener));
        }
        else {
            this.listenerIterationCopy.remove(listener);
        }
        final EventListeners<T> parent = this.parentSupplier.get();
        if (parent != null) {
            parent.remove(listener);
        }
    }
    
    @API
    public synchronized boolean contains(final T listener) {
        return this.listeners.contains(listener);
    }
    
    @API
    public synchronized void invoke(final Consumer<T> invoker) {
        if (this.iterating) {
            throw new IllegalStateException("Recursive invocation of event listeners is unsupported");
        }
        try {
            this.iterating = true;
            for (final T listener : this.listenerIterationCopy) {
                invoker.accept(listener);
            }
        }
        finally {
            this.iterating = false;
            for (final Runnable pendingModification : this.pendingCopyModifications) {
                pendingModification.run();
            }
            this.pendingCopyModifications.clear();
        }
    }
    
    @API
    public synchronized boolean isEmpty() {
        return this.listeners.isEmpty();
    }
    
    @API
    public synchronized void clear() {
        this.listeners.clear();
    }
}

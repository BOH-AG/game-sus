// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IOrderedStack;

public abstract class CircleStack<E> implements IOrderedStack<E>
{
    private final Object[] pool;
    private int index;
    private final int size;
    private final Object[] container;
    
    public CircleStack(final int argStackSize, final int argContainerSize) {
        this.size = argStackSize;
        this.pool = new Object[argStackSize];
        for (int i = 0; i < argStackSize; ++i) {
            this.pool[i] = this.newInstance();
        }
        this.index = 0;
        this.container = new Object[argContainerSize];
    }
    
    @Override
    public final E pop() {
        ++this.index;
        if (this.index >= this.size) {
            this.index = 0;
        }
        return (E)this.pool[this.index];
    }
    
    @Override
    public final E[] pop(final int argNum) {
        assert argNum <= this.container.length : "Container array is too small";
        if (this.index + argNum < this.size) {
            System.arraycopy(this.pool, this.index, this.container, 0, argNum);
            this.index += argNum;
        }
        else {
            final int overlap = this.index + argNum - this.size;
            System.arraycopy(this.pool, this.index, this.container, 0, argNum - overlap);
            System.arraycopy(this.pool, 0, this.container, argNum - overlap, overlap);
            this.index = overlap;
        }
        return (E[])this.container;
    }
    
    @Override
    public void push(final int argNum) {
    }
    
    protected abstract E newInstance();
}

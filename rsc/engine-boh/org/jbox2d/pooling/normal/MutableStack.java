// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IDynamicStack;

public abstract class MutableStack<E> implements IDynamicStack<E>
{
    private E[] stack;
    private int index;
    private int size;
    
    public MutableStack(final int argInitSize) {
        this.index = 0;
        this.stack = null;
        this.index = 0;
        this.extendStack(argInitSize);
    }
    
    private void extendStack(final int argSize) {
        final E[] newStack = this.newArray(argSize);
        if (this.stack != null) {
            System.arraycopy(this.stack, 0, newStack, 0, this.size);
        }
        for (int i = 0; i < newStack.length; ++i) {
            newStack[i] = this.newInstance();
        }
        this.stack = newStack;
        this.size = newStack.length;
    }
    
    @Override
    public final E pop() {
        if (this.index >= this.size) {
            this.extendStack(this.size * 2);
        }
        return this.stack[this.index++];
    }
    
    @Override
    public final void push(final E argObject) {
        assert this.index > 0;
        this.stack[--this.index] = argObject;
    }
    
    protected abstract E newInstance();
    
    protected abstract E[] newArray(final int p0);
}

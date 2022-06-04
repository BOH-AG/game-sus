// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling;

public interface IOrderedStack<E>
{
    E pop();
    
    E[] pop(final int p0);
    
    void push(final int p0);
}

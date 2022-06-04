// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.stacks;

public class DynamicIntStack
{
    private int[] stack;
    private int size;
    private int position;
    
    public DynamicIntStack(final int initialSize) {
        this.stack = new int[initialSize];
        this.position = 0;
        this.size = initialSize;
    }
    
    public void reset() {
        this.position = 0;
    }
    
    public int pop() {
        assert this.position > 0;
        final int[] stack = this.stack;
        final int position = this.position - 1;
        this.position = position;
        return stack[position];
    }
    
    public void push(final int i) {
        if (this.position == this.size) {
            final int[] old = this.stack;
            this.stack = new int[this.size * 2];
            this.size = this.stack.length;
            System.arraycopy(old, 0, this.stack, 0, old.length);
        }
        this.stack[this.position++] = i;
    }
    
    public int getCount() {
        return this.position;
    }
}

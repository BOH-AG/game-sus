// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.lang.reflect.Array;

public class BufferUtils
{
    public static <T> T[] reallocateBuffer(final Class<T> klass, final T[] oldBuffer, final int oldCapacity, final int newCapacity) {
        assert newCapacity > oldCapacity;
        final T[] newBuffer = (T[])Array.newInstance(klass, newCapacity);
        if (oldBuffer != null) {
            System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
        }
        for (int i = oldCapacity; i < newCapacity; ++i) {
            try {
                newBuffer[i] = klass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return newBuffer;
    }
    
    public static int[] reallocateBuffer(final int[] oldBuffer, final int oldCapacity, final int newCapacity) {
        assert newCapacity > oldCapacity;
        final int[] newBuffer = new int[newCapacity];
        if (oldBuffer != null) {
            System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
        }
        return newBuffer;
    }
    
    public static float[] reallocateBuffer(final float[] oldBuffer, final int oldCapacity, final int newCapacity) {
        assert newCapacity > oldCapacity;
        final float[] newBuffer = new float[newCapacity];
        if (oldBuffer != null) {
            System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
        }
        return newBuffer;
    }
    
    public static <T> T[] reallocateBuffer(final Class<T> klass, T[] buffer, final int userSuppliedCapacity, final int oldCapacity, final int newCapacity, final boolean deferred) {
        assert newCapacity > oldCapacity;
        assert newCapacity <= userSuppliedCapacity;
        if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
            buffer = reallocateBuffer(klass, buffer, oldCapacity, newCapacity);
        }
        return buffer;
    }
    
    public static int[] reallocateBuffer(int[] buffer, final int userSuppliedCapacity, final int oldCapacity, final int newCapacity, final boolean deferred) {
        assert newCapacity > oldCapacity;
        assert newCapacity <= userSuppliedCapacity;
        if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
            buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
        }
        return buffer;
    }
    
    public static float[] reallocateBuffer(float[] buffer, final int userSuppliedCapacity, final int oldCapacity, final int newCapacity, final boolean deferred) {
        assert newCapacity > oldCapacity;
        assert newCapacity <= userSuppliedCapacity;
        if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
            buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
        }
        return buffer;
    }
    
    public static <T> void rotate(final T[] ray, int first, int new_first, final int last) {
        int next = new_first;
        while (next != first) {
            final T temp = ray[first];
            ray[first] = ray[next];
            ray[next] = temp;
            ++first;
            if (++next == last) {
                next = new_first;
            }
            else {
                if (first != new_first) {
                    continue;
                }
                new_first = next;
            }
        }
    }
    
    public static void rotate(final int[] ray, int first, int new_first, final int last) {
        int next = new_first;
        while (next != first) {
            final int temp = ray[first];
            ray[first] = ray[next];
            ray[next] = temp;
            ++first;
            if (++next == last) {
                next = new_first;
            }
            else {
                if (first != new_first) {
                    continue;
                }
                new_first = next;
            }
        }
    }
    
    public static void rotate(final float[] ray, int first, int new_first, final int last) {
        int next = new_first;
        while (next != first) {
            final float temp = ray[first];
            ray[first] = ray[next];
            ray[next] = temp;
            ++first;
            if (++next == last) {
                next = new_first;
            }
            else {
                if (first != new_first) {
                    continue;
                }
                new_first = next;
            }
        }
    }
}

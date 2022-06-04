// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

class PlatformMathUtils
{
    private static final float SHIFT23 = 8388608.0f;
    private static final float INV_SHIFT23 = 1.1920929E-7f;
    
    public static final float fastPow(final float a, float b) {
        float x = (float)Float.floatToRawIntBits(a);
        x *= 1.1920929E-7f;
        x -= 127.0f;
        float y = x - ((x >= 0.0f) ? ((int)x) : ((int)x - 1));
        b *= x + (y - y * y) * 0.346607f;
        y = b - ((b >= 0.0f) ? ((int)b) : ((int)b - 1));
        y = (y - y * y) * 0.33971f;
        return Float.intBitsToFloat((int)((b + 127.0f - y) * 8388608.0f));
    }
}

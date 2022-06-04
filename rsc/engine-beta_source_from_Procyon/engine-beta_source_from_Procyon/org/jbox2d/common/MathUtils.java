// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.util.Random;

public class MathUtils extends PlatformMathUtils
{
    public static final float PI = 3.1415927f;
    public static final float TWOPI = 6.2831855f;
    public static final float INV_PI = 0.31830987f;
    public static final float HALF_PI = 1.5707964f;
    public static final float QUARTER_PI = 0.7853982f;
    public static final float THREE_HALVES_PI = 4.712389f;
    public static final float DEG2RAD = 0.017453292f;
    public static final float RAD2DEG = 57.295776f;
    public static final float[] sinLUT;
    
    public static final float sin(final float x) {
        if (Settings.SINCOS_LUT_ENABLED) {
            return sinLUT(x);
        }
        return (float)StrictMath.sin(x);
    }
    
    public static final float sinLUT(float x) {
        x %= 6.2831855f;
        if (x < 0.0f) {
            x += 6.2831855f;
        }
        if (!Settings.SINCOS_LUT_LERP) {
            return MathUtils.sinLUT[round(x / 1.1E-4f) % Settings.SINCOS_LUT_LENGTH];
        }
        x /= 1.1E-4f;
        final int index = (int)x;
        if (index != 0) {
            x %= index;
        }
        if (index == Settings.SINCOS_LUT_LENGTH - 1) {
            return (1.0f - x) * MathUtils.sinLUT[index] + x * MathUtils.sinLUT[0];
        }
        return (1.0f - x) * MathUtils.sinLUT[index] + x * MathUtils.sinLUT[index + 1];
    }
    
    public static final float cos(final float x) {
        if (Settings.SINCOS_LUT_ENABLED) {
            return sinLUT(1.5707964f - x);
        }
        return (float)StrictMath.cos(x);
    }
    
    public static final float abs(final float x) {
        if (Settings.FAST_ABS) {
            return (x > 0.0f) ? x : (-x);
        }
        return StrictMath.abs(x);
    }
    
    public static final float fastAbs(final float x) {
        return (x > 0.0f) ? x : (-x);
    }
    
    public static final int abs(final int x) {
        final int y = x >> 31;
        return (x ^ y) - y;
    }
    
    public static final int floor(final float x) {
        if (Settings.FAST_FLOOR) {
            return fastFloor(x);
        }
        return (int)StrictMath.floor(x);
    }
    
    public static final int fastFloor(final float x) {
        final int y = (int)x;
        if (x < y) {
            return y - 1;
        }
        return y;
    }
    
    public static final int ceil(final float x) {
        if (Settings.FAST_CEIL) {
            return fastCeil(x);
        }
        return (int)StrictMath.ceil(x);
    }
    
    public static final int fastCeil(final float x) {
        final int y = (int)x;
        if (x > y) {
            return y + 1;
        }
        return y;
    }
    
    public static final int round(final float x) {
        if (Settings.FAST_ROUND) {
            return floor(x + 0.5f);
        }
        return StrictMath.round(x);
    }
    
    public static final int ceilPowerOf2(final int x) {
        int pow2;
        for (pow2 = 1; pow2 < x; pow2 <<= 1) {}
        return pow2;
    }
    
    public static final float max(final float a, final float b) {
        return (a > b) ? a : b;
    }
    
    public static final int max(final int a, final int b) {
        return (a > b) ? a : b;
    }
    
    public static final float min(final float a, final float b) {
        return (a < b) ? a : b;
    }
    
    public static final int min(final int a, final int b) {
        return (a < b) ? a : b;
    }
    
    public static final float map(final float val, final float fromMin, final float fromMax, final float toMin, final float toMax) {
        final float mult = (val - fromMin) / (fromMax - fromMin);
        final float res = toMin + mult * (toMax - toMin);
        return res;
    }
    
    public static final float clamp(final float a, final float low, final float high) {
        return max(low, min(a, high));
    }
    
    public static final Vec2 clamp(final Vec2 a, final Vec2 low, final Vec2 high) {
        final Vec2 min = new Vec2();
        min.x = ((a.x < high.x) ? a.x : high.x);
        min.y = ((a.y < high.y) ? a.y : high.y);
        min.x = ((low.x > min.x) ? low.x : min.x);
        min.y = ((low.y > min.y) ? low.y : min.y);
        return min;
    }
    
    public static final void clampToOut(final Vec2 a, final Vec2 low, final Vec2 high, final Vec2 dest) {
        dest.x = ((a.x < high.x) ? a.x : high.x);
        dest.y = ((a.y < high.y) ? a.y : high.y);
        dest.x = ((low.x > dest.x) ? low.x : dest.x);
        dest.y = ((low.y > dest.y) ? low.y : dest.y);
    }
    
    public static final int nextPowerOfTwo(int x) {
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }
    
    public static final boolean isPowerOfTwo(final int x) {
        return x > 0 && (x & x - 1) == 0x0;
    }
    
    public static final float pow(final float a, final float b) {
        if (Settings.FAST_POW) {
            return PlatformMathUtils.fastPow(a, b);
        }
        return (float)StrictMath.pow(a, b);
    }
    
    public static final float atan2(final float y, final float x) {
        if (Settings.FAST_ATAN2) {
            return fastAtan2(y, x);
        }
        return (float)StrictMath.atan2(y, x);
    }
    
    public static final float fastAtan2(final float y, final float x) {
        if (x != 0.0f) {
            final float z = y / x;
            float atan;
            if (abs(z) < 1.0f) {
                atan = z / (1.0f + 0.28f * z * z);
                if (x < 0.0f) {
                    if (y < 0.0f) {
                        return atan - 3.1415927f;
                    }
                    return atan + 3.1415927f;
                }
            }
            else {
                atan = 1.5707964f - z / (z * z + 0.28f);
                if (y < 0.0f) {
                    return atan - 3.1415927f;
                }
            }
            return atan;
        }
        if (y > 0.0f) {
            return 1.5707964f;
        }
        if (y == 0.0f) {
            return 0.0f;
        }
        return -1.5707964f;
    }
    
    public static final float reduceAngle(float theta) {
        theta %= 6.2831855f;
        if (abs(theta) > 3.1415927f) {
            theta -= 6.2831855f;
        }
        if (abs(theta) > 1.5707964f) {
            theta = 3.1415927f - theta;
        }
        return theta;
    }
    
    public static final float randomFloat(final float argLow, final float argHigh) {
        return (float)Math.random() * (argHigh - argLow) + argLow;
    }
    
    public static final float randomFloat(final Random r, final float argLow, final float argHigh) {
        return r.nextFloat() * (argHigh - argLow) + argLow;
    }
    
    public static final float sqrt(final float x) {
        return (float)StrictMath.sqrt(x);
    }
    
    public static final float distanceSquared(final Vec2 v1, final Vec2 v2) {
        final float dx = v1.x - v2.x;
        final float dy = v1.y - v2.y;
        return dx * dx + dy * dy;
    }
    
    public static final float distance(final Vec2 v1, final Vec2 v2) {
        return sqrt(distanceSquared(v1, v2));
    }
    
    static {
        sinLUT = new float[Settings.SINCOS_LUT_LENGTH];
        for (int i = 0; i < Settings.SINCOS_LUT_LENGTH; ++i) {
            MathUtils.sinLUT[i] = (float)Math.sin(i * 1.1E-4f);
        }
    }
}

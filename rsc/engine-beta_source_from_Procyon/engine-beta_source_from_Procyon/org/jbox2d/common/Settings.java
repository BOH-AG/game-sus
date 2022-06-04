// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public class Settings
{
    public static final float EPSILON = 1.1920929E-7f;
    public static final float PI = 3.1415927f;
    public static boolean FAST_ABS;
    public static boolean FAST_FLOOR;
    public static boolean FAST_CEIL;
    public static boolean FAST_ROUND;
    public static boolean FAST_ATAN2;
    public static boolean FAST_POW;
    public static int CONTACT_STACK_INIT_SIZE;
    public static boolean SINCOS_LUT_ENABLED;
    public static final float SINCOS_LUT_PRECISION = 1.1E-4f;
    public static final int SINCOS_LUT_LENGTH;
    public static boolean SINCOS_LUT_LERP;
    public static int maxManifoldPoints;
    public static int maxPolygonVertices;
    public static float aabbExtension;
    public static float aabbMultiplier;
    public static float linearSlop;
    public static float angularSlop;
    public static float polygonRadius;
    public static int maxSubSteps;
    public static int maxTOIContacts;
    public static float velocityThreshold;
    public static float maxLinearCorrection;
    public static float maxAngularCorrection;
    public static float maxTranslation;
    public static float maxTranslationSquared;
    public static float maxRotation;
    public static float maxRotationSquared;
    public static float baumgarte;
    public static float toiBaugarte;
    public static float timeToSleep;
    public static float linearSleepTolerance;
    public static float angularSleepTolerance;
    public static final int invalidParticleIndex = -1;
    public static final float particleStride = 0.75f;
    public static final float minParticleWeight = 1.0f;
    public static final float maxParticleWeight = 5.0f;
    public static final int maxTriadDistance = 2;
    public static final int maxTriadDistanceSquared = 4;
    public static final int minParticleBufferCapacity = 256;
    
    public static float mixFriction(final float friction1, final float friction2) {
        return MathUtils.sqrt(friction1 * friction2);
    }
    
    public static float mixRestitution(final float restitution1, final float restitution2) {
        return (restitution1 > restitution2) ? restitution1 : restitution2;
    }
    
    static {
        Settings.FAST_ABS = true;
        Settings.FAST_FLOOR = true;
        Settings.FAST_CEIL = true;
        Settings.FAST_ROUND = true;
        Settings.FAST_ATAN2 = true;
        Settings.FAST_POW = true;
        Settings.CONTACT_STACK_INIT_SIZE = 10;
        Settings.SINCOS_LUT_ENABLED = true;
        SINCOS_LUT_LENGTH = (int)Math.ceil(57119.86598277577);
        Settings.SINCOS_LUT_LERP = false;
        Settings.maxManifoldPoints = 2;
        Settings.maxPolygonVertices = 8;
        Settings.aabbExtension = 0.1f;
        Settings.aabbMultiplier = 2.0f;
        Settings.linearSlop = 0.005f;
        Settings.angularSlop = 0.03490659f;
        Settings.polygonRadius = 2.0f * Settings.linearSlop;
        Settings.maxSubSteps = 8;
        Settings.maxTOIContacts = 32;
        Settings.velocityThreshold = 1.0f;
        Settings.maxLinearCorrection = 0.2f;
        Settings.maxAngularCorrection = 0.13962635f;
        Settings.maxTranslation = 2.0f;
        Settings.maxTranslationSquared = Settings.maxTranslation * Settings.maxTranslation;
        Settings.maxRotation = 1.5707964f;
        Settings.maxRotationSquared = Settings.maxRotation * Settings.maxRotation;
        Settings.baumgarte = 0.2f;
        Settings.toiBaugarte = 0.75f;
        Settings.timeToSleep = 0.5f;
        Settings.linearSleepTolerance = 0.01f;
        Settings.angularSleepTolerance = 0.03490659f;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.common.MathUtils;
import java.util.List;

public class Profile
{
    private static final int LONG_AVG_NUMS = 20;
    private static final float LONG_FRACTION = 0.05f;
    private static final int SHORT_AVG_NUMS = 5;
    private static final float SHORT_FRACTION = 0.2f;
    public final ProfileEntry step;
    public final ProfileEntry stepInit;
    public final ProfileEntry collide;
    public final ProfileEntry solveParticleSystem;
    public final ProfileEntry solve;
    public final ProfileEntry solveInit;
    public final ProfileEntry solveVelocity;
    public final ProfileEntry solvePosition;
    public final ProfileEntry broadphase;
    public final ProfileEntry solveTOI;
    
    public Profile() {
        this.step = new ProfileEntry();
        this.stepInit = new ProfileEntry();
        this.collide = new ProfileEntry();
        this.solveParticleSystem = new ProfileEntry();
        this.solve = new ProfileEntry();
        this.solveInit = new ProfileEntry();
        this.solveVelocity = new ProfileEntry();
        this.solvePosition = new ProfileEntry();
        this.broadphase = new ProfileEntry();
        this.solveTOI = new ProfileEntry();
    }
    
    public void toDebugStrings(final List<String> strings) {
        strings.add("Profile:");
        strings.add(" step: " + this.step);
        strings.add("  init: " + this.stepInit);
        strings.add("  collide: " + this.collide);
        strings.add("  particles: " + this.solveParticleSystem);
        strings.add("  solve: " + this.solve);
        strings.add("   solveInit: " + this.solveInit);
        strings.add("   solveVelocity: " + this.solveVelocity);
        strings.add("   solvePosition: " + this.solvePosition);
        strings.add("   broadphase: " + this.broadphase);
        strings.add("  solveTOI: " + this.solveTOI);
    }
    
    public static class ProfileEntry
    {
        float longAvg;
        float shortAvg;
        float min;
        float max;
        float accum;
        
        public ProfileEntry() {
            this.min = Float.MAX_VALUE;
            this.max = -3.4028235E38f;
        }
        
        public void record(final float value) {
            this.longAvg = this.longAvg * 0.95f + value * 0.05f;
            this.shortAvg = this.shortAvg * 0.8f + value * 0.2f;
            this.min = MathUtils.min(value, this.min);
            this.max = MathUtils.max(value, this.max);
        }
        
        public void startAccum() {
            this.accum = 0.0f;
        }
        
        public void accum(final float value) {
            this.accum += value;
        }
        
        public void endAccum() {
            this.record(this.accum);
        }
        
        @Override
        public String toString() {
            return String.format("%.2f (%.2f) [%.2f,%.2f]", this.shortAvg, this.longAvg, this.min, this.max);
        }
    }
}

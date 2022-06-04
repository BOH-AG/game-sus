// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

public class GearJointDef extends JointDef
{
    public Joint joint1;
    public Joint joint2;
    public float ratio;
    
    public GearJointDef() {
        super(JointType.GEAR);
        this.joint1 = null;
        this.joint2 = null;
    }
}

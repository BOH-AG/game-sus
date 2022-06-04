// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.API;

public final class PrismaticJoint extends Joint<org.jbox2d.dynamics.joints.PrismaticJoint>
{
    private float lowerLimit;
    private float upperLimit;
    private boolean motorEnabled;
    private boolean limitEnabled;
    private float motorSpeed;
    private float maximumMotorForce;
    
    @API
    public void setMaximumMotorForce(final float maximumMotorForce) {
        this.maximumMotorForce = maximumMotorForce;
        this.motorEnabled = true;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.setMaxMotorForce(maximumMotorForce);
            joint.enableMotor(true);
        }
    }
    
    @API
    public float getMaximumMotorForce() {
        return this.maximumMotorForce;
    }
    
    @API
    public float getLowerLimit() {
        return this.lowerLimit;
    }
    
    @API
    public void setLowerLimit(final float lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.setLimits(lowerLimit, this.upperLimit);
            joint.enableLimit(true);
        }
    }
    
    @API
    public float getUpperLimit() {
        return this.upperLimit;
    }
    
    @API
    public void setUpperLimit(final float upperLimit) {
        this.upperLimit = upperLimit;
        this.limitEnabled = true;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.setLimits(this.lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }
    
    @API
    public float getMotorSpeed() {
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            return joint.getMotorSpeed();
        }
        return this.motorSpeed;
    }
    
    @API
    public void setMotorSpeed(final float motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.setMotorSpeed(motorSpeed);
            joint.enableMotor(true);
        }
    }
    
    @API
    public boolean isMotorEnabled() {
        return this.motorEnabled;
    }
    
    @API
    public void setMotorEnabled(final boolean motorEnabled) {
        this.motorEnabled = motorEnabled;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.enableMotor(motorEnabled);
        }
    }
    
    @API
    public boolean isLimitEnabled() {
        return this.limitEnabled;
    }
    
    @API
    public void setLimitEnabled(final boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint != null) {
            joint.enableLimit(limitEnabled);
        }
    }
    
    @API
    public void setLimits(final float lower, final float upper) {
        this.setLowerLimit(lower);
        this.setUpperLimit(upper);
    }
    
    @API
    public float getTranslation() {
        final org.jbox2d.dynamics.joints.PrismaticJoint joint = this.getJoint();
        if (joint == null) {
            return 0.0f;
        }
        return joint.getJointTranslation();
    }
    
    @Override
    protected void updateCustomProperties(final org.jbox2d.dynamics.joints.PrismaticJoint joint) {
        joint.setMotorSpeed(this.motorSpeed);
        joint.setMaxMotorForce(this.maximumMotorForce);
        joint.setLimits(this.lowerLimit, this.upperLimit);
        joint.enableMotor(this.motorEnabled);
        joint.enableLimit(this.limitEnabled);
    }
}

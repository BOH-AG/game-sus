// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.API;

public final class RevoluteJoint extends Joint<org.jbox2d.dynamics.joints.RevoluteJoint>
{
    private float lowerLimit;
    private float upperLimit;
    private boolean motorEnabled;
    private boolean limitEnabled;
    private float motorSpeed;
    private float maximumMotorTorque;
    
    @API
    public void setMaximumMotorTorque(final float maximumMotorTorque) {
        this.maximumMotorTorque = maximumMotorTorque;
        this.motorEnabled = true;
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
        if (joint != null) {
            joint.setMaxMotorTorque(maximumMotorTorque);
            joint.enableMotor(true);
        }
    }
    
    @API
    public float getMaximumMotorTorque() {
        return this.maximumMotorTorque;
    }
    
    @API
    public float getLowerLimit() {
        return this.lowerLimit;
    }
    
    @API
    public void setLowerLimit(final float lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
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
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
        if (joint != null) {
            joint.setLimits(this.lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }
    
    @API
    public float getMotorSpeed() {
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
        if (joint != null) {
            return (float)Math.toDegrees(joint.getMotorSpeed()) / 360.0f;
        }
        return this.motorSpeed;
    }
    
    @API
    public void setMotorSpeed(final float motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
        if (joint != null) {
            joint.setMotorSpeed((float)Math.toRadians(motorSpeed * 360.0f));
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
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
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
        final org.jbox2d.dynamics.joints.RevoluteJoint joint = this.getJoint();
        if (joint != null) {
            joint.enableMotor(limitEnabled);
        }
    }
    
    @Override
    protected void updateCustomProperties(final org.jbox2d.dynamics.joints.RevoluteJoint joint) {
        joint.setMotorSpeed((float)Math.toRadians(this.motorSpeed * 360.0f));
        joint.setMaxMotorTorque(this.maximumMotorTorque);
        joint.setLimits(this.lowerLimit, this.upperLimit);
        joint.enableLimit(this.limitEnabled);
        joint.enableMotor(this.motorEnabled);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.common.Vec2;

public class BodyDef
{
    public BodyType type;
    public Object userData;
    public Vec2 position;
    public float angle;
    public Vec2 linearVelocity;
    public float angularVelocity;
    public float linearDamping;
    public float angularDamping;
    public boolean allowSleep;
    public boolean awake;
    public boolean fixedRotation;
    public boolean bullet;
    public boolean active;
    public float gravityScale;
    
    public BodyDef() {
        this.userData = null;
        this.position = new Vec2();
        this.angle = 0.0f;
        this.linearVelocity = new Vec2();
        this.angularVelocity = 0.0f;
        this.linearDamping = 0.0f;
        this.angularDamping = 0.0f;
        this.allowSleep = true;
        this.awake = true;
        this.fixedRotation = false;
        this.bullet = false;
        this.type = BodyType.STATIC;
        this.active = true;
        this.gravityScale = 1.0f;
    }
    
    public BodyType getType() {
        return this.type;
    }
    
    public void setType(final BodyType type) {
        this.type = type;
    }
    
    public Object getUserData() {
        return this.userData;
    }
    
    public void setUserData(final Object userData) {
        this.userData = userData;
    }
    
    public Vec2 getPosition() {
        return this.position;
    }
    
    public void setPosition(final Vec2 position) {
        this.position = position;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public Vec2 getLinearVelocity() {
        return this.linearVelocity;
    }
    
    public void setLinearVelocity(final Vec2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }
    
    public float getAngularVelocity() {
        return this.angularVelocity;
    }
    
    public void setAngularVelocity(final float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    public float getLinearDamping() {
        return this.linearDamping;
    }
    
    public void setLinearDamping(final float linearDamping) {
        this.linearDamping = linearDamping;
    }
    
    public float getAngularDamping() {
        return this.angularDamping;
    }
    
    public void setAngularDamping(final float angularDamping) {
        this.angularDamping = angularDamping;
    }
    
    public boolean isAllowSleep() {
        return this.allowSleep;
    }
    
    public void setAllowSleep(final boolean allowSleep) {
        this.allowSleep = allowSleep;
    }
    
    public boolean isAwake() {
        return this.awake;
    }
    
    public void setAwake(final boolean awake) {
        this.awake = awake;
    }
    
    public boolean isFixedRotation() {
        return this.fixedRotation;
    }
    
    public void setFixedRotation(final boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }
    
    public boolean isBullet() {
        return this.bullet;
    }
    
    public void setBullet(final boolean bullet) {
        this.bullet = bullet;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public float getGravityScale() {
        return this.gravityScale;
    }
    
    public void setGravityScale(final float gravityScale) {
        this.gravityScale = gravityScale;
    }
}

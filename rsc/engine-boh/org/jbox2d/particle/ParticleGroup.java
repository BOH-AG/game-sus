// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

public class ParticleGroup
{
    ParticleSystem m_system;
    int m_firstIndex;
    int m_lastIndex;
    int m_groupFlags;
    float m_strength;
    ParticleGroup m_prev;
    ParticleGroup m_next;
    int m_timestamp;
    float m_mass;
    float m_inertia;
    final Vec2 m_center;
    final Vec2 m_linearVelocity;
    float m_angularVelocity;
    final Transform m_transform;
    boolean m_destroyAutomatically;
    boolean m_toBeDestroyed;
    boolean m_toBeSplit;
    Object m_userData;
    
    public ParticleGroup() {
        this.m_center = new Vec2();
        this.m_linearVelocity = new Vec2();
        this.m_transform = new Transform();
        this.m_firstIndex = 0;
        this.m_lastIndex = 0;
        this.m_groupFlags = 0;
        this.m_strength = 1.0f;
        this.m_timestamp = -1;
        this.m_mass = 0.0f;
        this.m_inertia = 0.0f;
        this.m_angularVelocity = 0.0f;
        this.m_transform.setIdentity();
        this.m_destroyAutomatically = true;
        this.m_toBeDestroyed = false;
        this.m_toBeSplit = false;
    }
    
    public ParticleGroup getNext() {
        return this.m_next;
    }
    
    public int getParticleCount() {
        return this.m_lastIndex - this.m_firstIndex;
    }
    
    public int getBufferIndex() {
        return this.m_firstIndex;
    }
    
    public int getGroupFlags() {
        return this.m_groupFlags;
    }
    
    public void setGroupFlags(final int flags) {
        this.m_groupFlags = flags;
    }
    
    public float getMass() {
        this.updateStatistics();
        return this.m_mass;
    }
    
    public float getInertia() {
        this.updateStatistics();
        return this.m_inertia;
    }
    
    public Vec2 getCenter() {
        this.updateStatistics();
        return this.m_center;
    }
    
    public Vec2 getLinearVelocity() {
        this.updateStatistics();
        return this.m_linearVelocity;
    }
    
    public float getAngularVelocity() {
        this.updateStatistics();
        return this.m_angularVelocity;
    }
    
    public Transform getTransform() {
        return this.m_transform;
    }
    
    public Vec2 getPosition() {
        return this.m_transform.p;
    }
    
    public float getAngle() {
        return this.m_transform.q.getAngle();
    }
    
    public Object getUserData() {
        return this.m_userData;
    }
    
    public void setUserData(final Object data) {
        this.m_userData = data;
    }
    
    public void updateStatistics() {
        if (this.m_timestamp != this.m_system.m_timestamp) {
            final float m = this.m_system.getParticleMass();
            this.m_mass = 0.0f;
            this.m_center.setZero();
            this.m_linearVelocity.setZero();
            for (int i = this.m_firstIndex; i < this.m_lastIndex; ++i) {
                this.m_mass += m;
                final Vec2 pos = this.m_system.m_positionBuffer.data[i];
                final Vec2 center = this.m_center;
                center.x += m * pos.x;
                final Vec2 center2 = this.m_center;
                center2.y += m * pos.y;
                final Vec2 vel = this.m_system.m_velocityBuffer.data[i];
                final Vec2 linearVelocity = this.m_linearVelocity;
                linearVelocity.x += m * vel.x;
                final Vec2 linearVelocity2 = this.m_linearVelocity;
                linearVelocity2.y += m * vel.y;
            }
            if (this.m_mass > 0.0f) {
                final Vec2 center3 = this.m_center;
                center3.x *= 1.0f / this.m_mass;
                final Vec2 center4 = this.m_center;
                center4.y *= 1.0f / this.m_mass;
                final Vec2 linearVelocity3 = this.m_linearVelocity;
                linearVelocity3.x *= 1.0f / this.m_mass;
                final Vec2 linearVelocity4 = this.m_linearVelocity;
                linearVelocity4.y *= 1.0f / this.m_mass;
            }
            this.m_inertia = 0.0f;
            this.m_angularVelocity = 0.0f;
            for (int i = this.m_firstIndex; i < this.m_lastIndex; ++i) {
                final Vec2 pos = this.m_system.m_positionBuffer.data[i];
                final Vec2 vel = this.m_system.m_velocityBuffer.data[i];
                final float px = pos.x - this.m_center.x;
                final float py = pos.y - this.m_center.y;
                final float vx = vel.x - this.m_linearVelocity.x;
                final float vy = vel.y - this.m_linearVelocity.y;
                this.m_inertia += m * (px * px + py * py);
                this.m_angularVelocity += m * (px * vy - py * vx);
            }
            if (this.m_inertia > 0.0f) {
                this.m_angularVelocity *= 1.0f / this.m_inertia;
            }
            this.m_timestamp = this.m_system.m_timestamp;
        }
    }
}

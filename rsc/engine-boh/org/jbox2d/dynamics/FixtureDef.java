// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.collision.shapes.Shape;

public class FixtureDef
{
    public Shape shape;
    public Object userData;
    public float friction;
    public float restitution;
    public float density;
    public boolean isSensor;
    public Filter filter;
    
    public FixtureDef() {
        this.shape = null;
        this.shape = null;
        this.userData = null;
        this.friction = 0.2f;
        this.restitution = 0.0f;
        this.density = 0.0f;
        this.filter = new Filter();
        this.isSensor = false;
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public void setShape(final Shape shape) {
        this.shape = shape;
    }
    
    public Object getUserData() {
        return this.userData;
    }
    
    public void setUserData(final Object userData) {
        this.userData = userData;
    }
    
    public float getFriction() {
        return this.friction;
    }
    
    public void setFriction(final float friction) {
        this.friction = friction;
    }
    
    public float getRestitution() {
        return this.restitution;
    }
    
    public void setRestitution(final float restitution) {
        this.restitution = restitution;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public boolean isSensor() {
        return this.isSensor;
    }
    
    public void setSensor(final boolean isSensor) {
        this.isSensor = isSensor;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
}

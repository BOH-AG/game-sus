// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

import ea.Vector;
import ea.internal.annotations.Internal;

@Internal
public final class Bounds
{
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    
    public Bounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Bounds withCenterAtBoundsCenter(final Bounds r) {
        return this.withCenterPoint(r.getCenter());
    }
    
    public Bounds withCenterPoint(final Vector p) {
        return this.moveBy(p.subtract(this.getCenter()));
    }
    
    public Vector getCenter() {
        return new Vector(this.x + this.width / 2.0f, this.y + this.height / 2.0f);
    }
    
    public Bounds moveBy(final Vector v) {
        return new Bounds(this.x + v.getX(), this.y + v.getY(), this.width, this.height);
    }
    
    public Bounds smallestCommon(final Bounds bounds) {
        float x;
        if (bounds.x < this.x) {
            x = bounds.x;
        }
        else {
            x = this.x;
        }
        float y;
        if (bounds.y < this.y) {
            y = bounds.y;
        }
        else {
            y = this.y;
        }
        float dX;
        if (bounds.x + bounds.width > this.x + this.width) {
            dX = bounds.x + bounds.width - x;
        }
        else {
            dX = this.x + this.width - x;
        }
        float dY;
        if (bounds.y + bounds.height > this.y + this.height) {
            dY = bounds.y + bounds.height - y;
        }
        else {
            dY = this.y + this.height - y;
        }
        return new Bounds(x, y, dX, dY);
    }
    
    public Bounds above(final float lowerBound) {
        if (this.y + this.height < lowerBound) {
            return this;
        }
        return new Bounds(this.x, lowerBound - this.height, this.width, this.height);
    }
    
    public Bounds below(final float upperBound) {
        if (this.y > upperBound) {
            return this;
        }
        return new Bounds(this.x, upperBound, this.width, this.height);
    }
    
    public Bounds rightOf(final float border) {
        if (this.x > border) {
            return this;
        }
        return new Bounds(border, this.y, this.width, this.height);
    }
    
    public Bounds leftOf(final float border) {
        if (this.x + this.width < border) {
            return this;
        }
        return new Bounds(border - this.width, this.y, this.width, this.height);
    }
    
    public Bounds atPosition(final float realX, final float realY) {
        return new Bounds(realX, realY, this.width, this.height);
    }
    
    public boolean contains(final Vector v) {
        return v.getX() >= this.x && v.getY() >= this.y && v.getX() <= this.x + this.width && v.getY() <= this.y + this.height;
    }
    
    public Vector[] points() {
        return new Vector[] { new Vector(this.x, this.y), new Vector(this.x + this.width, this.y), new Vector(this.x, this.y + this.height), new Vector(this.x + this.width, this.y + this.height) };
    }
    
    public boolean contains(final Bounds innen) {
        return this.x <= innen.x && this.y <= innen.y && this.x + this.width >= innen.x + innen.width && this.y + this.height >= innen.y + innen.height;
    }
    
    public boolean above(final Bounds r) {
        return this.y < r.y;
    }
    
    public Bounds in(final Bounds aussen) {
        float realX = this.x;
        float realY = this.y;
        if (this.x < aussen.x) {
            realX = aussen.x;
        }
        if (this.x + this.width > aussen.x + aussen.width) {
            realX = aussen.x + aussen.width - this.width;
        }
        if (this.y < aussen.y) {
            realY = aussen.y;
        }
        if (this.y + this.height > aussen.y + aussen.height) {
            realY = aussen.y + aussen.height - this.height;
        }
        return new Bounds(realX, realY, this.width, this.height);
    }
    
    public Bounds clone() {
        return new Bounds(this.x, this.y, this.width, this.height);
    }
    
    @Override
    public String toString() {
        return "Bounding-Rectangle: getX:" + this.x + " getY: " + this.y + " getDX: " + this.width + " getDY: " + this.height;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public Vector getPosition() {
        return new Vector(this.x, this.y);
    }
}

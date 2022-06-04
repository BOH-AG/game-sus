// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import ea.internal.annotations.Internal;
import org.jbox2d.common.Vec2;
import ea.internal.annotations.API;

@API
public final class Vector implements Cloneable
{
    @API
    public static final Vector NULL;
    @API
    public static final Vector RIGHT;
    @API
    public static final Vector LEFT;
    @API
    public static final Vector UP;
    @API
    public static final Vector DOWN;
    private final float x;
    private final float y;
    
    @Internal
    public static Vector of(final Vec2 vector) {
        return new Vector(vector.x, vector.y);
    }
    
    @API
    public Vector(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    @API
    public Vector(final double x, final double y) {
        this((float)x, (float)y);
    }
    
    @API
    public Vector(final Vector start, final Vector end) {
        this.x = end.x - start.x;
        this.y = end.y - start.y;
    }
    
    @API
    public float getX() {
        return this.x;
    }
    
    @API
    public float getY() {
        return this.y;
    }
    
    @API
    public Vector normalize() {
        return this.divide(this.getLength());
    }
    
    @API
    public Vector divide(final float divisor) {
        if (divisor == 0.0f) {
            throw new ArithmeticException("Der Divisor f\u00fcr das Teilen war 0");
        }
        return new Vector(this.x / divisor, this.y / divisor);
    }
    
    @API
    public float getLength() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    @API
    public Vector negate() {
        return new Vector(-this.x, -this.y);
    }
    
    @API
    public Vector negateX() {
        return new Vector(-this.x, this.y);
    }
    
    @API
    public Vector negateY() {
        return new Vector(this.x, -this.y);
    }
    
    @API
    public Vector add(final float x, final float y) {
        return new Vector(this.x + x, this.y + y);
    }
    
    @API
    public Vector add(final Vector v) {
        return new Vector(this.x + v.x, this.y + v.y);
    }
    
    @API
    public Vector subtract(final float x, final float y) {
        return new Vector(this.x - x, this.y - y);
    }
    
    @API
    public Vector subtract(final Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }
    
    @API
    public Vector fromThisTo(final Vector v) {
        return v.subtract(this);
    }
    
    @API
    public Vector multiply(final float factor) {
        return new Vector(this.x * factor, this.y * factor);
    }
    
    @API
    public Vector multiplyX(final float factor) {
        return new Vector(this.x * factor, this.y);
    }
    
    @API
    public Vector multiplyY(final float factor) {
        return new Vector(this.x, this.y * factor);
    }
    
    @API
    public float getScalarProduct(final Vector v) {
        return this.x * v.x + this.y * v.y;
    }
    
    @API
    public boolean isNull() {
        return this.x == 0.0f && this.y == 0.0f;
    }
    
    @API
    public boolean isIntegral() {
        return this.x == (int)this.x && this.y == (int)this.y;
    }
    
    @API
    public Direction getDirection() {
        if (this.x == 0.0f && this.y == 0.0f) {
            return Direction.NONE;
        }
        if (this.x == 0.0f) {
            return (this.y > 0.0f) ? Direction.DOWN : Direction.UP;
        }
        if (this.y == 0.0f) {
            return (this.x > 0.0f) ? Direction.RIGHT : Direction.LEFT;
        }
        if (this.y < 0.0f) {
            return (this.x < 0.0f) ? Direction.UP_LEFT : Direction.UP_RIGHT;
        }
        return (this.x > 0.0f) ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
    }
    
    @API
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Vector) {
            final Vector v = (Vector)o;
            return this.x == v.x && this.y == v.y;
        }
        return false;
    }
    
    public Vector clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    public String toString() {
        return "ea.Vector [ x = " + this.x + "; y = " + this.y + " ]";
    }
    
    @API
    public float getManhattanLength() {
        final float length = this.x + this.y;
        return (length < 0.0f) ? (-length) : length;
    }
    
    @API
    public float getAngle(final Vector other) {
        return (float)Math.toDegrees(Math.acos(this.getScalarProduct(other) / (this.getLength() * other.getLength())));
    }
    
    @Internal
    public Vec2 toVec2() {
        return new Vec2(this.x, this.y);
    }
    
    static {
        NULL = new Vector(0.0f, 0.0f);
        RIGHT = new Vector(1.0f, 0.0f);
        LEFT = new Vector(-1.0f, 0.0f);
        UP = new Vector(0.0f, -1.0f);
        DOWN = new Vector(0.0f, 1.0f);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import java.awt.Point;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.actor.Actor;
import ea.internal.Bounds;

public final class Camera
{
    public static final float DEFAULT_ZOOM = 30.0f;
    private Vector position;
    private Bounds bounds;
    private Actor focus;
    private Vector offset;
    private float zoom;
    private float rotation;
    
    @Internal
    public Camera() {
        this.focus = null;
        this.offset = Vector.NULL;
        this.zoom = 30.0f;
        this.rotation = 0.0f;
        this.position = new Vector(0.0f, 0.0f);
    }
    
    @API
    public void setFocus(final Actor focus) {
        this.focus = focus;
    }
    
    @API
    public boolean hasFocus() {
        return this.focus != null;
    }
    
    @API
    public void setOffset(final Vector offset) {
        this.offset = offset;
    }
    
    @API
    public Vector getOffset() {
        return this.offset;
    }
    
    @API
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }
    
    @API
    public boolean hasBounds() {
        return this.bounds != null;
    }
    
    @API
    public void setZoom(final float zoom) {
        if (zoom <= 0.0f) {
            throw new IllegalArgumentException("Der Kamerazoom kann nicht kleiner oder gleich 0 sein.");
        }
        this.zoom = zoom;
    }
    
    @API
    public float getZoom() {
        return this.zoom;
    }
    
    @API
    public void moveBy(final float x, final float y) {
        this.moveBy(new Vector(x, y));
    }
    
    @API
    public void moveBy(final Vector vector) {
        this.position = this.position.add(vector);
    }
    
    @API
    public void moveTo(final int x, final int y) {
        this.moveTo(new Vector((float)x, (float)y));
    }
    
    @API
    public void moveTo(final Vector vector) {
        this.position = vector;
    }
    
    @API
    public void rotateBy(final float radians) {
        this.rotation += radians;
    }
    
    @API
    public void rotateTo(final float radians) {
        this.rotation = radians;
    }
    
    @API
    public Vector getPosition() {
        return this.moveIntoBounds(this.position.add(this.offset));
    }
    
    @Internal
    public Point toScreenPixelLocation(final Vector locationInWorld, final float pixelPerMeter) {
        final Vector locationInWorldCameraRelative = this.position.fromThisTo(locationInWorld);
        final Vector cameraRelativeLocInPx = this.position.multiply(pixelPerMeter);
        final Vector frameSize = Game.getFrameSizeInPixels();
        return new Point((int)(frameSize.getX() / 2.0f + cameraRelativeLocInPx.getX()), (int)(frameSize.getY() / 2.0f + cameraRelativeLocInPx.getY()));
    }
    
    public void onFrameUpdate() {
        if (this.hasFocus()) {
            this.position = this.focus.getCenter();
        }
        this.position = this.moveIntoBounds(this.position);
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    private Vector moveIntoBounds(final Vector position) {
        if (!this.hasBounds()) {
            return position;
        }
        final float x = Math.max(this.bounds.getX(), Math.min(position.getX(), this.bounds.getX() + this.bounds.getWidth()));
        final float y = Math.max(this.bounds.getY(), Math.min(position.getY(), this.bounds.getY() + this.bounds.getHeight()));
        return new Vector(x, y);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public class OBBViewportTransform implements IViewportTransform
{
    protected final OBB box;
    private boolean yFlip;
    private final Mat22 yFlipMat;
    private final Mat22 inv;
    private final Mat22 inv2;
    
    public OBBViewportTransform() {
        this.box = new OBB();
        this.yFlip = false;
        this.yFlipMat = new Mat22(1.0f, 0.0f, 0.0f, -1.0f);
        this.inv = new Mat22();
        this.inv2 = new Mat22();
        this.box.R.setIdentity();
    }
    
    public void set(final OBBViewportTransform vpt) {
        this.box.center.set(vpt.box.center);
        this.box.extents.set(vpt.box.extents);
        this.box.R.set(vpt.box.R);
        this.yFlip = vpt.yFlip;
    }
    
    @Override
    public void setCamera(final float x, final float y, final float scale) {
        this.box.center.set(x, y);
        Mat22.createScaleTransform(scale, this.box.R);
    }
    
    @Override
    public Vec2 getExtents() {
        return this.box.extents;
    }
    
    @Override
    public Mat22 getMat22Representation() {
        return this.box.R;
    }
    
    @Override
    public void setExtents(final Vec2 argExtents) {
        this.box.extents.set(argExtents);
    }
    
    @Override
    public void setExtents(final float halfWidth, final float halfHeight) {
        this.box.extents.set(halfWidth, halfHeight);
    }
    
    @Override
    public Vec2 getCenter() {
        return this.box.center;
    }
    
    @Override
    public void setCenter(final Vec2 argPos) {
        this.box.center.set(argPos);
    }
    
    @Override
    public void setCenter(final float x, final float y) {
        this.box.center.set(x, y);
    }
    
    public Mat22 getTransform() {
        return this.box.R;
    }
    
    public void setTransform(final Mat22 transform) {
        this.box.R.set(transform);
    }
    
    @Override
    public void mulByTransform(final Mat22 transform) {
        this.box.R.mulLocal(transform);
    }
    
    @Override
    public boolean isYFlip() {
        return this.yFlip;
    }
    
    @Override
    public void setYFlip(final boolean yFlip) {
        this.yFlip = yFlip;
    }
    
    @Override
    public void getScreenVectorToWorld(final Vec2 screen, final Vec2 world) {
        this.box.R.invertToOut(this.inv);
        this.inv.mulToOut(screen, world);
        if (this.yFlip) {
            this.yFlipMat.mulToOut(world, world);
        }
    }
    
    @Override
    public void getWorldVectorToScreen(final Vec2 world, final Vec2 screen) {
        this.box.R.mulToOut(world, screen);
        if (this.yFlip) {
            this.yFlipMat.mulToOut(screen, screen);
        }
    }
    
    @Override
    public void getWorldToScreen(final Vec2 world, final Vec2 screen) {
        screen.x = world.x - this.box.center.x;
        screen.y = world.y - this.box.center.y;
        this.box.R.mulToOut(screen, screen);
        if (this.yFlip) {
            this.yFlipMat.mulToOut(screen, screen);
        }
        screen.x += this.box.extents.x;
        screen.y += this.box.extents.y;
    }
    
    @Override
    public void getScreenToWorld(final Vec2 screen, final Vec2 world) {
        world.x = screen.x - this.box.extents.x;
        world.y = screen.y - this.box.extents.y;
        if (this.yFlip) {
            this.yFlipMat.mulToOut(world, world);
        }
        this.box.R.invertToOut(this.inv2);
        this.inv2.mulToOut(world, world);
        world.x += this.box.center.x;
        world.y += this.box.center.y;
    }
    
    public static class OBB
    {
        public final Mat22 R;
        public final Vec2 center;
        public final Vec2 extents;
        
        public OBB() {
            this.R = new Mat22();
            this.center = new Vec2();
            this.extents = new Vec2();
        }
    }
}

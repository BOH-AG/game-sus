// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.particle.ParticleColor;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.IViewportTransform;

public abstract class DebugDraw
{
    public static final int e_shapeBit = 2;
    public static final int e_jointBit = 4;
    public static final int e_aabbBit = 8;
    public static final int e_pairBit = 16;
    public static final int e_centerOfMassBit = 32;
    public static final int e_dynamicTreeBit = 64;
    public static final int e_wireframeDrawingBit = 128;
    protected int m_drawFlags;
    protected IViewportTransform viewportTransform;
    
    public DebugDraw() {
        this(null);
    }
    
    public DebugDraw(final IViewportTransform viewport) {
        this.m_drawFlags = 0;
        this.viewportTransform = viewport;
    }
    
    public void setViewportTransform(final IViewportTransform viewportTransform) {
        this.viewportTransform = viewportTransform;
    }
    
    public void setFlags(final int flags) {
        this.m_drawFlags = flags;
    }
    
    public int getFlags() {
        return this.m_drawFlags;
    }
    
    public void appendFlags(final int flags) {
        this.m_drawFlags |= flags;
    }
    
    public void clearFlags(final int flags) {
        this.m_drawFlags &= ~flags;
    }
    
    public void drawPolygon(final Vec2[] vertices, final int vertexCount, final Color3f color) {
        if (vertexCount == 1) {
            this.drawSegment(vertices[0], vertices[0], color);
            return;
        }
        for (int i = 0; i < vertexCount - 1; ++i) {
            this.drawSegment(vertices[i], vertices[i + 1], color);
        }
        if (vertexCount > 2) {
            this.drawSegment(vertices[vertexCount - 1], vertices[0], color);
        }
    }
    
    public abstract void drawPoint(final Vec2 p0, final float p1, final Color3f p2);
    
    public abstract void drawSolidPolygon(final Vec2[] p0, final int p1, final Color3f p2);
    
    public abstract void drawCircle(final Vec2 p0, final float p1, final Color3f p2);
    
    public void drawCircle(final Vec2 center, final float radius, final Vec2 axis, final Color3f color) {
        this.drawCircle(center, radius, color);
    }
    
    public abstract void drawSolidCircle(final Vec2 p0, final float p1, final Vec2 p2, final Color3f p3);
    
    public abstract void drawSegment(final Vec2 p0, final Vec2 p1, final Color3f p2);
    
    public abstract void drawTransform(final Transform p0);
    
    public abstract void drawString(final float p0, final float p1, final String p2, final Color3f p3);
    
    public abstract void drawParticles(final Vec2[] p0, final float p1, final ParticleColor[] p2, final int p3);
    
    public abstract void drawParticlesWireframe(final Vec2[] p0, final float p1, final ParticleColor[] p2, final int p3);
    
    public void flush() {
    }
    
    public void drawString(final Vec2 pos, final String s, final Color3f color) {
        this.drawString(pos.x, pos.y, s, color);
    }
    
    public IViewportTransform getViewportTranform() {
        return this.viewportTransform;
    }
    
    @Deprecated
    public void setCamera(final float x, final float y, final float scale) {
        this.viewportTransform.setCamera(x, y, scale);
    }
    
    public void getScreenToWorldToOut(final Vec2 argScreen, final Vec2 argWorld) {
        this.viewportTransform.getScreenToWorld(argScreen, argWorld);
    }
    
    public void getWorldToScreenToOut(final Vec2 argWorld, final Vec2 argScreen) {
        this.viewportTransform.getWorldToScreen(argWorld, argScreen);
    }
    
    public void getWorldToScreenToOut(final float worldX, final float worldY, final Vec2 argScreen) {
        argScreen.set(worldX, worldY);
        this.viewportTransform.getWorldToScreen(argScreen, argScreen);
    }
    
    public Vec2 getWorldToScreen(final Vec2 argWorld) {
        final Vec2 screen = new Vec2();
        this.viewportTransform.getWorldToScreen(argWorld, screen);
        return screen;
    }
    
    public Vec2 getWorldToScreen(final float worldX, final float worldY) {
        final Vec2 argScreen = new Vec2(worldX, worldY);
        this.viewportTransform.getWorldToScreen(argScreen, argScreen);
        return argScreen;
    }
    
    public void getScreenToWorldToOut(final float screenX, final float screenY, final Vec2 argWorld) {
        argWorld.set(screenX, screenY);
        this.viewportTransform.getScreenToWorld(argWorld, argWorld);
    }
    
    public Vec2 getScreenToWorld(final Vec2 argScreen) {
        final Vec2 world = new Vec2();
        this.viewportTransform.getScreenToWorld(argScreen, world);
        return world;
    }
    
    public Vec2 getScreenToWorld(final float screenX, final float screenY) {
        final Vec2 screen = new Vec2(screenX, screenY);
        this.viewportTransform.getScreenToWorld(screen, screen);
        return screen;
    }
}

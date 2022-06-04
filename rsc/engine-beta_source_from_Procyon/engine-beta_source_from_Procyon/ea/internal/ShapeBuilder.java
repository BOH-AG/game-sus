// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.function.Supplier;
import ea.Vector;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;

public final class ShapeBuilder
{
    public static Shape createSimpleRectangularShape(final float width, final float height) {
        final PolygonShape shape = new PolygonShape();
        shape.set(new Vec2[] { new Vec2(0.0f, 0.0f), new Vec2(0.0f, height), new Vec2(width, height), new Vec2(width, 0.0f) }, 4);
        shape.m_centroid.set(new Vec2(width / 2.0f, height / 2.0f));
        return shape;
    }
    
    public static Shape createCircleShape(final float mx, final float my, final float r) {
        final CircleShape circleShape = new CircleShape();
        circleShape.m_p.set(mx, my);
        circleShape.setRadius(r);
        return circleShape;
    }
    
    public static Shape createPolygonShape(final Vector... points) {
        if (points.length < 3) {
            throw new IllegalArgumentException("Eine polygonale Shape ben\u00f6tigt mindestens 3 Punkte.");
        }
        final Vec2[] vec2s = new Vec2[points.length];
        for (int i = 0; i < points.length; ++i) {
            vec2s[i] = points[i].toVec2();
        }
        final PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vec2s, vec2s.length);
        return polygonShape;
    }
    
    public static Shape createAxisParallelRectangularShape(final float sx, final float sy, final float width, final float height) {
        final PolygonShape rectShape = new PolygonShape();
        rectShape.set(new Vec2[] { new Vec2(sx, sy), new Vec2(sx, sy + height), new Vec2(sx + width, sy + height), new Vec2(sx + width, sy) }, 4);
        return rectShape;
    }
    
    public static Supplier<List<Shape>> fromString(final String code) {
        code.replace(" ", "");
        final Scanner scanner = new Scanner(code);
        scanner.useDelimiter("&");
        final ArrayList<Shape> shapeList = new ArrayList<Shape>();
        while (scanner.hasNext()) {
            final String line = scanner.next();
            final Shape shape = fromLine(line);
            shapeList.add(shape);
        }
        return (Supplier<List<Shape>>)(() -> shapeList);
    }
    
    private static Shape fromLine(String line) {
        final char shape = line.charAt(0);
        line = line.substring(1);
        final String[] split = line.split(",");
        switch (shape) {
            case 'R': {
                if (split.length != 4) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                final float sx = Float.parseFloat(split[0]);
                final float sy = Float.parseFloat(split[1]);
                final float width = Float.parseFloat(split[2]);
                final float height = Float.parseFloat(split[3]);
                return createAxisParallelRectangularShape(sx, sy, width, height);
            }
            case 'P': {
                if (split.length % 2 != 0) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                final Vec2[] polyPoints = new Vec2[split.length / 2];
                for (int i = 0; i < polyPoints.length; ++i) {
                    final float px = Float.parseFloat(split[2 * i]);
                    final float py = Float.parseFloat(split[2 * i + 1]);
                    polyPoints[i] = new Vec2(px, py);
                }
                final PolygonShape polygonShape = new PolygonShape();
                polygonShape.set(polyPoints, polyPoints.length);
                return polygonShape;
            }
            case 'C': {
                if (split.length != 3) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                return createCircleShape(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
            }
            default: {
                throw new IllegalArgumentException("Fehlerhafte Eingabe!");
            }
        }
    }
}

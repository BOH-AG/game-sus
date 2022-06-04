// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.collision.shapes.Shape;
import ea.internal.annotations.Internal;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import ea.internal.annotations.API;
import ea.internal.ShapeBuilder;

public class TileContainer extends Actor implements TileMap
{
    private final Tile[][] tiles;
    private final float tileWidth;
    private final float tileHeight;
    
    @API
    public TileContainer(final int numX, final int numY, final float tileWidth, final float tileHeight) {
        super(() -> ShapeBuilder.createSimpleRectangularShape(tileWidth * numX, tileHeight * numY));
        if (numX <= 0 || numY <= 0) {
            throw new IllegalArgumentException("numX und numY m\u00fcssen jeweils > 0 sein.");
        }
        if (tileWidth <= 0.0f || tileHeight <= 0.0f) {
            throw new IllegalArgumentException("Breite und H\u00f6he der Tiles m\u00fcssen jeweils > 0 sein.");
        }
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new Tile[numX][numY];
    }
    
    public int getTileCountX() {
        return this.tiles.length;
    }
    
    public int getTileCountY() {
        return this.tiles[0].length;
    }
    
    @API
    public TileContainer(final int numX, final int numY, final float tileSize) {
        this(numX, numY, tileSize, tileSize);
    }
    
    @API
    public void setTile(final int x, final int y, final Tile tile) {
        this.tiles[x][y] = tile;
    }
    
    @Internal
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        final AffineTransform ore = g.getTransform();
        final float offset = this.tiles[0].length * this.tileHeight * pixelPerMeter;
        g.translate(0.0, -offset);
        for (int x = 0; x < this.tiles.length; ++x) {
            for (int y = 0; y < this.tiles[0].length; ++y) {
                if (this.tiles[x][y] != null) {
                    final float tx = this.tileWidth * x * pixelPerMeter;
                    final float ty = this.tileHeight * y * pixelPerMeter;
                    g.translate(tx, ty);
                    this.tiles[x][y].render(g, this.tileWidth * pixelPerMeter, this.tileHeight * pixelPerMeter);
                    g.translate(-tx, -ty);
                }
            }
        }
        g.setTransform(ore);
    }
    
    @Override
    public Tile getTile(final int x, final int y) {
        return this.tiles[x][y];
    }
}

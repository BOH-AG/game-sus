// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import ea.internal.io.ImageLoader;
import ea.internal.annotations.API;

@API
public interface TileMap
{
    @API
    default Tile createFromImage(final String path) {
        return createFromImage(ImageLoader.load(path));
    }
    
    default Tile createFromImage(final BufferedImage image) {
        final AffineTransform pre;
        return (g, width, height) -> {
            pre = g.getTransform();
            g.scale(width / image.getWidth(), height / image.getHeight());
            g.drawImage(image, null, 0, 0);
            g.setTransform(pre);
        };
    }
    
    default TileMap createFromImage(final String path, final int sizeX, final int sizeY) {
        final BufferedImage image = ImageLoader.load(path);
        final Tile[][] tiles = new Tile[image.getWidth() / sizeX][image.getHeight() / sizeY];
        for (int posX = 0; posX < image.getWidth(); posX += sizeX) {
            for (int posY = 0; posY < image.getHeight(); posY += sizeY) {
                tiles[posX / sizeX][posY / sizeY] = createFromImage(image.getSubimage(posX, posY, sizeX, sizeY));
            }
        }
        return (x, y) -> tiles[x][y];
    }
    
    Tile getTile(final int p0, final int p1);
}

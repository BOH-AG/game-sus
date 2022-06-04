// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.Internal;
import java.awt.Graphics2D;
import ea.internal.annotations.API;

@API
public interface Tile
{
    @Internal
    void render(final Graphics2D p0, final float p1, final float p2);
}

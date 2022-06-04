// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu.event;

import ea.internal.annotations.API;
import ea.edu.EduActor;

@API
public interface KollisionsReagierbar<T extends EduActor>
{
    @API
    boolean kollisionReagieren(final T p0);
}

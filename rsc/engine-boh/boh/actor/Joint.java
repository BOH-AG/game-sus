// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.Internal;
import ea.event.EventListeners;
import ea.internal.physics.WorldHandler;
import javafx.util.Pair;
import ea.internal.annotations.API;

@API
public abstract class Joint<JointType extends org.jbox2d.dynamics.joints.Joint>
{
    private Pair<JointType, WorldHandler> joint;
    private final EventListeners<Runnable> releaseListeners;
    
    public Joint() {
        this.releaseListeners = new EventListeners<Runnable>();
    }
    
    @Internal
    public final void setJoint(final JointType joint, final WorldHandler worldHandler) {
        this.joint = (Pair<JointType, WorldHandler>)new Pair((Object)joint, (Object)worldHandler);
        this.updateCustomProperties(joint);
    }
    
    protected abstract void updateCustomProperties(final JointType p0);
    
    @Internal
    protected final JointType getJoint() {
        final Pair<JointType, WorldHandler> joint = this.joint;
        if (joint == null) {
            return null;
        }
        return (JointType)joint.getKey();
    }
    
    @API
    public void release() {
        if (this.joint != null) {
            ((WorldHandler)this.joint.getValue()).getWorld().destroyJoint((org.jbox2d.dynamics.joints.Joint)this.joint.getKey());
            this.joint = null;
        }
        this.releaseListeners.invoke(Runnable::run);
        this.releaseListeners.clear();
    }
    
    @API
    public void addReleaseListener(final Runnable runnable) {
        this.releaseListeners.add(runnable);
    }
}

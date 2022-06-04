// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

public interface JointBuilder<JointType extends Joint>
{
    JointType createJoint(final World p0, final Body p1, final Body p2);
}

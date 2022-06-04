// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import java.util.ArrayList;

public class ConstantVolumeJointDef extends JointDef
{
    public float frequencyHz;
    public float dampingRatio;
    ArrayList<Body> bodies;
    ArrayList<DistanceJoint> joints;
    
    public ConstantVolumeJointDef() {
        super(JointType.CONSTANT_VOLUME);
        this.bodies = new ArrayList<Body>();
        this.joints = null;
        this.collideConnected = false;
        this.frequencyHz = 0.0f;
        this.dampingRatio = 0.0f;
    }
    
    public void addBody(final Body argBody) {
        this.bodies.add(argBody);
        if (this.bodies.size() == 1) {
            this.bodyA = argBody;
        }
        if (this.bodies.size() == 2) {
            this.bodyB = argBody;
        }
    }
    
    public void addBodyAndJoint(final Body argBody, final DistanceJoint argJoint) {
        this.addBody(argBody);
        if (this.joints == null) {
            this.joints = new ArrayList<DistanceJoint>();
        }
        this.joints.add(argJoint);
    }
}

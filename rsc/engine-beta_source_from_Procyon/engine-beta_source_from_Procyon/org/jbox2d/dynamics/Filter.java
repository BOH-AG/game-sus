// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

public class Filter
{
    public int categoryBits;
    public int maskBits;
    public int groupIndex;
    
    public Filter() {
        this.categoryBits = 1;
        this.maskBits = 65535;
        this.groupIndex = 0;
    }
    
    public void set(final Filter argOther) {
        this.categoryBits = argOther.categoryBits;
        this.maskBits = argOther.maskBits;
        this.groupIndex = argOther.groupIndex;
    }
}

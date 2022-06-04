// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import java.util.concurrent.ThreadLocalRandom;
import ea.internal.annotations.Internal;
import ea.internal.annotations.API;

@API
public final class Random
{
    @Internal
    private Random() {
    }
    
    @API
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
    
    @API
    public static int nextInteger(final int upperLimit) {
        if (upperLimit < 0) {
            throw new IllegalArgumentException("Achtung! F\u00fcr eine Zufallszahl muss die definierte Obergrenze (die inklusiv in der Ergebnismenge ist) eine nichtnegative Zahl sein!");
        }
        return ThreadLocalRandom.current().nextInt(upperLimit + 1);
    }
    
    @API
    public static int nextInteger(final int lowerLimit, final int upperLimit) {
        if (lowerLimit == upperLimit) {
            return lowerLimit;
        }
        if (lowerLimit < upperLimit) {
            return lowerLimit + ThreadLocalRandom.current().nextInt(upperLimit - lowerLimit + 1);
        }
        return upperLimit + ThreadLocalRandom.current().nextInt(lowerLimit - upperLimit + 1);
    }
    
    @API
    public static float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }
}

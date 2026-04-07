
package com.ucm.server.statistics;

import java.util.EnumMap;
import java.util.Map;

public class FoldFrequency {
    private Map<Street, Double> frequency = new EnumMap<>(Street.class);
    private Map<Street, Integer> foldCount = new EnumMap<>(Street.class);
    private Map<Street, Integer> handsSeen = new EnumMap<>(Street.class);

    public void update(Street street, boolean folded) {
        handsSeen.put(street, handsSeen.getOrDefault(street, 0) + 1);
        if (folded) foldCount.put(street, foldCount.getOrDefault(street, 0) + 1);
        frequency.put(street, (double) foldCount.get(street) / handsSeen.get(street));
    }

    public double get(Street street) {
        return frequency.getOrDefault(street, 0.0);
    }
    
}

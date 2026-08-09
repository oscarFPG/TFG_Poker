
package com.ucm.server.statistics;

import java.util.EnumMap;
import java.util.Map;

import com.ucm.common.PokerStreet;


public class FoldFrequency {

    private Map<PokerStreet, Double> frequency = new EnumMap<>(PokerStreet.class);
    private Map<PokerStreet, Integer> foldCount = new EnumMap<>(PokerStreet.class);
    private Map<PokerStreet, Integer> handsSeen = new EnumMap<>(PokerStreet.class);

    public void update(PokerStreet street, boolean folded) {
        
        handsSeen.put(street, handsSeen.getOrDefault(street, 0) + 1);
        
        if (folded)
            foldCount.put(street, foldCount.getOrDefault(street, 0) + 1);
        
        frequency.put(street, (double) foldCount.get(street) / handsSeen.get(street));
    }

    public double get(PokerStreet street) {
        return frequency.getOrDefault(street, 0.0);
    }
    
}
package ru.equalizationofgeodeticnetworks.network.builder;

import ru.equalizationofgeodeticnetworks.model.*;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.model.traverse.LevelingTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.PolygonometricTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.Traverse;

import java.util.*;

@Component
public class NetworkIndexBuilder {
    public Map<String, Integer> buildIndexMap(List<Traverse> traverses) {
        Map<String, Integer> map = new HashMap<>();
        for (Traverse t : traverses) {
            List<String> unknowns = null;
            if (t instanceof PolygonometricTraverse) {
                unknowns = ((PolygonometricTraverse) t).getUnknownNames();
            } else if (t instanceof LevelingTraverse) {
                unknowns = ((LevelingTraverse) t).getUnknownNames();
            }
            if (unknowns != null) {
                for (String name : unknowns) {
                    map.putIfAbsent(name, map.size());
                }
            }
        }
        return map;
    }
}

package ru.equalizationofgeodeticnetworks.core.approx.approxProviderImpl;

import ru.equalizationofgeodeticnetworks.core.approx.InitialApproxProvider;
import ru.equalizationofgeodeticnetworks.model.point.Point;
import ru.equalizationofgeodeticnetworks.model.traverse.LevelingTraverse;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class LevelingApproxProvider implements InitialApproxProvider {
    @Override
    public Map<String, Point> computeApprox(List<?> rawMeasurements,
                                            Map<String, Point> fixedPoints,
                                            Map<String, Double> startDirs,
                                            Map<String, Double> endDirs,
                                            List<String> unknownNames) {
        Map<String, Double> known = new HashMap<>();
        for (Map.Entry<String, Point> e : fixedPoints.entrySet()) {
            known.put(e.getKey(), e.getValue().getHeight());
        }
        for (Object obj : rawMeasurements) {
            if (obj instanceof LevelingTraverse.RawHeightDiff rh) {
                if (known.containsKey(rh.from) && !known.containsKey(rh.to)) {
                    known.put(rh.to, known.get(rh.from) + rh.value);
                } else if (!known.containsKey(rh.from) && known.containsKey(rh.to)) {
                    known.put(rh.from, known.get(rh.to) - rh.value);
                }
            }
        }
        Map<String, Point> result = new HashMap<>();
        for (String name : unknownNames) {
            Double h = known.get(name);
            if (h == null) throw new IllegalStateException("Не удалось вычислить приближение для " + name);
            result.put(name, new Point(h));
        }
        return result;
    }
}

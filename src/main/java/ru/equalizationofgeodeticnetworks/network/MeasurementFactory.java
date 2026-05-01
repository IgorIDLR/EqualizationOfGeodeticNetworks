package ru.equalizationofgeodeticnetworks.network;

import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.model.measurement.AngleMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.DistanceMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.HeightMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.model.point.Point;
import ru.equalizationofgeodeticnetworks.model.traverse.LevelingTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.PolygonometricTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.Traverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MeasurementFactory {
    public List<Measurement> createMeasurements(List<Traverse> traverses, Map<String, Integer> indexMap) {
        List<Measurement> all = new ArrayList<>();
        for (Traverse t : traverses) {
            if (t instanceof PolygonometricTraverse) {
                PolygonometricTraverse pt = (PolygonometricTraverse) t;
                Map<String, Point> fixed = pt.getFixedPoints();
                Map<String, Double> startDirs = pt.getStartDirs();
                Map<String, Double> endDirs = pt.getEndDirs();
                for (Object obj : pt.getRawMeasurementsInOrder()) {
                    if (obj instanceof PolygonometricTraverse.RawDistance rd) {
                        int idx1 = indexMap.getOrDefault(rd.getP1(), -1);
                        int idx2 = indexMap.getOrDefault(rd.getP2(), -1);
                        Point p1 = (idx1 == -1) ? fixed.get(rd.getP1()) : null;
                        Point p2 = (idx2 == -1) ? fixed.get(rd.getP2()) : null;
                        double weight = rd.getWeight();
                        String name = rd.getP1() + "-" + rd.getP2();
                        if (idx1 >= 0 && idx2 >= 0)
                            all.add(new DistanceMeasurement(name, rd.getValue(), weight, idx1, idx2));
                        else if (idx1 >= 0)
                            all.add(new DistanceMeasurement(name, rd.getValue(), weight, idx1, p2));
                        else if (idx2 >= 0)
                            all.add(new DistanceMeasurement(name, rd.getValue(), weight, p1, idx2));
                        else
                            all.add(new DistanceMeasurement(name, rd.getValue(), weight, p1, p2));
                    } else if (obj instanceof PolygonometricTraverse.RawAngle ra) {
                        int idxStat = indexMap.getOrDefault(ra.getStation(), -1);
                        int idxBack = (ra.getBack() == null) ? -1 : indexMap.getOrDefault(ra.getBack(), -1);
                        int idxForw = (ra.getForward() == null) ? -1 : indexMap.getOrDefault(ra.getForward(), -1);
                        Point statFixed = (idxStat == -1) ? fixed.get(ra.getStation()) : null;
                        Point backFixed = (idxBack == -1 && ra.getBack() != null) ? fixed.get(ra.getBack()) : null;
                        Point forwFixed = (idxForw == -1 && ra.getForward() != null) ? fixed.get(ra.getForward()) : null;
                        double weight = ra.getWeight();
                        double observed = Math.toRadians(ra.getValueDeg());
                        boolean useFixedDirBack = (ra.getBack() == null);
                        boolean useFixedDirForward = (ra.getForward() == null);
                        double fixedDirBack = useFixedDirBack ? startDirs.getOrDefault(ra.getStation(), 0.0) : 0.0;
                        double fixedDirForward = useFixedDirForward ? endDirs.getOrDefault(ra.getStation(), 0.0) : 0.0;
                        String name = "∠" + ra.getStation() + (ra.getBack() != null ? "(" + ra.getBack() + ")" : "(dir)") + "-" + (ra.getForward() != null ? ra.getForward() : "(dir)");
                        all.add(new AngleMeasurement(name, observed, weight,
                                idxStat, statFixed, idxBack, backFixed, idxForw, forwFixed,
                                useFixedDirBack, fixedDirBack, useFixedDirForward, fixedDirForward));
                    }
                }
            } else if (t instanceof LevelingTraverse) {
                LevelingTraverse lt = (LevelingTraverse) t;
                Map<String, Double> fixed = lt.getFixedHeights();
                for (LevelingTraverse.RawHeightDiff rh : lt.getRawDiffs()) {
                    int idxFrom = indexMap.getOrDefault(rh.from, -1);
                    int idxTo = indexMap.getOrDefault(rh.to, -1);
                    boolean fromFixed = (idxFrom == -1);
                    boolean toFixed = (idxTo == -1);
                    double fixedFrom = fromFixed ? fixed.getOrDefault(rh.from, 0.0) : 0.0;
                    double fixedTo = toFixed ? fixed.getOrDefault(rh.to, 0.0) : 0.0;
                    double weight = 1.0 / (rh.sigma * rh.sigma);
                    String name = rh.from + "-" + rh.to;
                    all.add(new HeightMeasurement(name, rh.value, weight,
                            idxFrom, idxTo, fixedFrom, fixedTo, fromFixed, toFixed));
                }
            }
        }
        return all;
    }
}

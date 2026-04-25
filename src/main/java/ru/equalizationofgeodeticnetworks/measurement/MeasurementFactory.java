package ru.equalizationofgeodeticnetworks.measurement;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeasurementFactory {

    private final NetworkData data;
    private final IndexMapper indexMapper;
    private final Map<String, Point> fixedPoints;

    public MeasurementFactory(NetworkData data, IndexMapper indexMapper) {
        this.data = data;
        this.indexMapper = indexMapper;
        this.fixedPoints = data.getFixedPoints();
    }

    public List<Measurement> createMeasurements() {
        List<Measurement> measurements = new ArrayList<>();
        for (NetworkData.RawMeasurement rm : data.getRawMeasurements()) {
            if (rm.type == NetworkData.RawMeasurement.Type.DISTANCE) {
                measurements.add(createDistance(rm));
            } else {
                measurements.add(createAngle(rm));
            }
        }
        return measurements;
    }

    private Measurement createDistance(NetworkData.RawMeasurement rm) {
        int idx1 = indexMapper.getIndex(rm.p1);
        int idx2 = indexMapper.getIndex(rm.p2);
        Point p1 = (idx1 == -1) ? fixedPoints.get(rm.p1) : null;
        Point p2 = (idx2 == -1) ? fixedPoints.get(rm.p2) : null;
        double weight = 1.0 / (rm.distSigma * rm.distSigma);
        String name = rm.p1 + "-" + rm.p2;

        if (idx1 >= 0 && idx2 >= 0) {
            return new DistanceMeasurement(name, rm.distValue, weight, idx1, idx2);
        } else if (idx1 >= 0) {
            return new DistanceMeasurement(name, rm.distValue, weight, idx1, p2);
        } else if (idx2 >= 0) {
            return new DistanceMeasurement(name, rm.distValue, weight, p1, idx2);
        } else {
            return new DistanceMeasurement(name, rm.distValue, weight, p1, p2);
        }
    }

    private Measurement createAngle(NetworkData.RawMeasurement rm) {
        int idxStat = indexMapper.getIndex(rm.station);
        int idxBack = (rm.back == null) ? -1 : indexMapper.getIndex(rm.back);
        int idxForw = (rm.forward == null) ? -1 : indexMapper.getIndex(rm.forward);

        Point statFixed = (idxStat == -1) ? fixedPoints.get(rm.station) : null;
        Point backFixed = (idxBack == -1 && rm.back != null) ? fixedPoints.get(rm.back) : null;
        Point forwFixed = (idxForw == -1 && rm.forward != null) ? fixedPoints.get(rm.forward) : null;

        double weight = 1.0 / (rm.angleSigma * rm.angleSigma);
        double observed = rm.angleValue;

        boolean useFixedDirBack = (rm.back == null);
        boolean useFixedDirForward = (rm.forward == null);
        double fixedDirBack = useFixedDirBack ? data.getStartDirs().getOrDefault(rm.station, 0.0) : 0.0;
        double fixedDirForward = useFixedDirForward ? data.getEndDirs().getOrDefault(rm.station, 0.0) : 0.0;

        if (useFixedDirBack && !data.getStartDirs().containsKey(rm.station))
            throw new IllegalArgumentException("Не задан начальный дирекционный угол для станции " + rm.station);
        if (useFixedDirForward && !data.getEndDirs().containsKey(rm.station))
            throw new IllegalArgumentException("Не задан конечный дирекционный угол для станции " + rm.station);

        String name = "∠" + rm.station;
        if (rm.back != null) name += "(" + rm.back + ")";
        else name += "(dir)";
        name += "-";
        if (rm.forward != null) name += rm.forward;
        else name += "(dir)";

        return new AngleMeasurement(name, observed, weight,
                idxStat, statFixed,
                idxBack, backFixed,
                idxForw, forwFixed,
                useFixedDirBack, fixedDirBack,
                useFixedDirForward, fixedDirForward);
    }
}

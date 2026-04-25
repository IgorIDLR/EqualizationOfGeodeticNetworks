package ru.equalizationofgeodeticnetworks.processor;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.Map;

public class DistanceProcessor implements MeasurementProcessor {
    @Override
    public ProcessingState process(ProcessingState state, NetworkData.RawMeasurement rm) {
        if (rm.type != NetworkData.RawMeasurement.Type.DISTANCE) {
            throw new IllegalArgumentException("DistanceProcessor получил не расстояние");
        }

        Map<String, Point> known = state.getKnown();
        Double currentDir = state.getCurrentDir();
        String currentStation = state.getCurrentStation();

        if (currentDir == null)
            throw new IllegalStateException("Нет текущего направления перед обработкой расстояния " + rm.p1 + "-" + rm.p2);

        boolean known1 = known.containsKey(rm.p1);
        boolean known2 = known.containsKey(rm.p2);

        if (known1 && !known2) {
            Point p1 = known.get(rm.p1);
            double x2 = p1.getX() + rm.distValue * Math.cos(currentDir);
            double y2 = p1.getY() + rm.distValue * Math.sin(currentDir);
            known.put(rm.p2, new Point(x2, y2));
            currentStation = rm.p2;
        } else if (!known1 && known2) {
            Point p2 = known.get(rm.p2);
            double backDir = currentDir + Math.PI;
            double x1 = p2.getX() + rm.distValue * Math.cos(backDir);
            double y1 = p2.getY() + rm.distValue * Math.sin(backDir);
            known.put(rm.p1, new Point(x1, y1));
            currentStation = rm.p1;
        } else if (known1 && known2) {
            // Контрольное измерение – обновляем текущую станцию, если одна из точек совпадает с ней
            if (rm.p1.equals(currentStation) && !rm.p2.equals(currentStation)) {
                currentStation = rm.p2;
            } else if (rm.p2.equals(currentStation) && !rm.p1.equals(currentStation)) {
                currentStation = rm.p1;
            }
        } else {
            throw new IllegalStateException("Обе точки неизвестны: " + rm.p1 + ", " + rm.p2);
        }

        state.setKnown(known);
        state.setCurrentDir(currentDir);
        state.setCurrentStation(currentStation);
        return state;
    }
}
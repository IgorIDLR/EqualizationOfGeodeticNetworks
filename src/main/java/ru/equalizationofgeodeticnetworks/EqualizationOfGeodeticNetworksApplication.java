package ru.equalizationofgeodeticnetworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.equalizationofgeodeticnetworks.adjustment.ParametricAdjustment;
import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.support.SupportDegreesConverter;
import ru.equalizationofgeodeticnetworks.support.SupportDimension;
import ru.equalizationofgeodeticnetworks.support.SupportNet;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EqualizationOfGeodeticNetworksApplication {

    public static void main(String[] args) {
        SpringApplication.run(EqualizationOfGeodeticNetworksApplication.class, args);
        SupportNet sNet = new SupportNet(3, 0.010, new ParametricAdjustment());

        var list = new ArrayList<Point>() {{
            add(new Point("H", 5419.866, 8258.602));
            add(new Point("K", 6384.933, 8518.732));

        }};

        sNet.addFixedPoints(list, 84.95297222, 296.0378056);

        var l2 = new ArrayList<SupportDimension>() {{
            add(new SupportDimension("H", 192.7223611, 893.244));
            add(new SupportDimension("A", 107.3257778, 793.531));
            add(new SupportDimension("B", 85.81344444, 1027.509));
            add(new SupportDimension("K", 185.2215556, 0));
        }};

        sNet.addDimensions(l2);

        var pol3 = new SupportNet(3, 0.01, new ParametricAdjustment());
        var l3 = new ArrayList<Point>(
                List.of(
                        new Point("A1", 2009.11, 2081.49),
                        new Point("A2", 2000.63, 2094.51),
                        new Point("A3", 1986.660, 2134.990),
                        new Point("A4", 1995.730, 2148.130)
                ));

        pol3.addFixedPoints(l3.get(0), l3.get(1), l3.get(2), l3.get(3));

        List<SupportDimension> pol3Unknown = List.of(
                new SupportDimension("A2", SupportDegreesConverter.degreesInAng(127, 33, 26), 16.600),
                new SupportDimension("2", SupportDegreesConverter.degreesInAng(225, 59, 24), 12.97),
                new SupportDimension("3", SupportDegreesConverter.degreesInAng(289, 37, 48), 15.08),
                new SupportDimension("4", SupportDegreesConverter.degreesInAng(51, 22, 41), 24.34),
                new SupportDimension("A3", SupportDegreesConverter.degreesInAng(137, 44, 1), 0)
        );

        pol3.addDimensions(pol3Unknown);

        pol3.solve();
    }
}

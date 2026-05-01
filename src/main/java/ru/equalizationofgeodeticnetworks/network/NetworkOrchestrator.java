package ru.equalizationofgeodeticnetworks.network;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.core.graph.GraphNetworkBuilder;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.model.traverse.Traverse;
import ru.equalizationofgeodeticnetworks.network.builder.InitialApproxBuilder;
import ru.equalizationofgeodeticnetworks.network.builder.NetworkIndexBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NetworkOrchestrator {

    private final NetworkIndexBuilder indexBuilder;
    private final InitialApproxBuilder approxBuilder;
    private final MeasurementFactory measurementFactory;
    private final NetworkSolver solver;
    private final GraphNetworkBuilder graphBuilder;

    public void execute(List<Traverse> traverses, double eps, int maxIter, boolean useFreeNetwork) {
        Map<String, Integer> indexMap = indexBuilder.buildIndexMap(traverses);
        double[] X = approxBuilder.buildApprox(traverses, indexMap);
        List<Measurement> measurements = measurementFactory.createMeasurements(traverses, indexMap);
        int n = X.length;
        double[][] Q = new double[n][n];
        for (int i = 0; i < n; i++) Q[i][i] = 1e10;
        solver.setUseFreeNetwork(useFreeNetwork);
        solver.solve(X, Q, measurements);
        // вывод результатов (упрощённо)
        System.out.println("Окончательные координаты:");
        for (Map.Entry<String, Integer> e : indexMap.entrySet()) {
            int idx = e.getValue();
            System.out.printf("%s: x = %.6f, y = %.6f\n", e.getKey(), X[idx*2], X[idx*2+1]);
        }
    }
}

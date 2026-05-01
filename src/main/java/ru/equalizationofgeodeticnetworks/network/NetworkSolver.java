package ru.equalizationofgeodeticnetworks.network;

import ru.equalizationofgeodeticnetworks.configuration.properties.AdjustmentProperties;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.FreeNetworkAdjustment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

@Slf4j
@Component
@RequiredArgsConstructor
public class NetworkSolver {

    private final AdjustmentEngine adjustmentEngine;
    private final FreeNetworkAdjustment freeNetworkSolver;
    private final AdjustmentProperties props;
    private boolean useFreeNetwork = false;

    public void setUseFreeNetwork(boolean use) { this.useFreeNetwork = use; }

    public void solve(double[] X, double[][] Q, List<Measurement> measurements) {
        if (useFreeNetwork) {
            freeNetworkSolver.adjustFree(X, Q, measurements, props.getEps(), props.getMaxIter());
        } else {
            adjustmentEngine.adjust(X, Q, measurements, props.getEps(), props.getMaxIter());
        }
    }
}

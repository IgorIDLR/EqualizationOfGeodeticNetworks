package ru.equalizationofgeodeticnetworks.network.builder;

import ru.equalizationofgeodeticnetworks.model.traverse.Traverse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.network.NetworkOrchestrator;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AdjustmentNetwork {
    private final NetworkOrchestrator orchestrator;
    private final List<Traverse> traverses = new ArrayList<>();

    public void addTraverse(Traverse traverse) { traverses.add(traverse); }

    public void solve() {
        orchestrator.execute(traverses, 1e-4, 20, false);
    }
}

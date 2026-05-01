package ru.equalizationofgeodeticnetworks.core.graph;

import ru.equalizationofgeodeticnetworks.model.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.model.measurement.AngleMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.DistanceMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.HeightMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.*;

@Component
public class GraphNetworkBuilder {
    private final boolean enabled;

    public GraphNetworkBuilder(boolean enabled) {
        this.enabled = enabled;
    }

    public Graph<String, DefaultEdge> buildGraph(List<Measurement> measurements, List<String> allPoints) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        allPoints.forEach(graph::addVertex);
        for (Measurement m : measurements) {
            if (m instanceof DistanceMeasurement dm) {
                String p1 = getPointName(dm, true);
                String p2 = getPointName(dm, false);
                if (p1 != null && p2 != null) graph.addEdge(p1, p2);
            } else if (m instanceof AngleMeasurement am) {
                String station = getPointName(am, true);
                String back = getPointName(am, false);
                String forward = getPointName(am, false, true);
                if (station != null && back != null) graph.addEdge(station, back);
                if (station != null && forward != null) graph.addEdge(station, forward);
            } else if (m instanceof HeightMeasurement hm) {
                String from = getPointName(hm, true);
                String to = getPointName(hm, false);
                if (from != null && to != null) graph.addEdge(from, to);
            }
        }
        return graph;
    }

    private String getPointName(DistanceMeasurement dm, boolean first) {
        if (first) {
            return dm.getIdx1() >= 0 ? "P" + dm.getIdx1() : (dm.getFixed1() != null ? "F" + dm.getFixed1().hashCode() : null);
        } else {
            return dm.getIdx2() >= 0 ? "P" + dm.getIdx2() : (dm.getFixed2() != null ? "F" + dm.getFixed2().hashCode() : null);
        }
    }

    private String getPointName(AngleMeasurement am, boolean station) {
        if (station) {
            return am.getIdxStation() >= 0 ? "P" + am.getIdxStation() : (am.getFixedStation() != null ? "F" + am.getFixedStation().hashCode() : null);
        } else {
            return am.getIdxBack() >= 0 ? "P" + am.getIdxBack() : (am.getFixedBack() != null ? "F" + am.getFixedBack().hashCode() : null);
        }
    }

    private String getPointName(AngleMeasurement am, boolean station, boolean forward) {
        if (forward) {
            return am.getIdxForward() >= 0 ? "P" + am.getIdxForward() : (am.getFixedForward() != null ? "F" + am.getFixedForward().hashCode() : null);
        }
        return getPointName(am, station);
    }

    private String getPointName(HeightMeasurement hm, boolean from) {
        if (from) {
            return hm.isFromFixed() ? "F" + hm.getFixedFrom() : (hm.getIdxFrom() >= 0 ? "P" + hm.getIdxFrom() : null);
        } else {
            return hm.isToFixed() ? "F" + hm.getFixedTo() : (hm.getIdxTo() >= 0 ? "P" + hm.getIdxTo() : null);
        }
    }

    public List<Set<String>> getConnectedComponents(Graph<String, DefaultEdge> graph) {
        ConnectivityInspector<String, DefaultEdge> inspector = new ConnectivityInspector<>(graph);
        return inspector.connectedSets();
    }

    public List<Measurement> orderMeasurementsTopologically(Graph<String, DefaultEdge> graph, List<Measurement> measurements) {
        if (!enabled) return measurements;
        CycleDetector<String, DefaultEdge> detector = new CycleDetector<>(graph);
        if (detector.detectCycles()) {
            measurements.sort((m1, m2) -> Integer.compare(countKnownPoints(m2), countKnownPoints(m1)));
            return measurements;
        }
        List<String> order = topologicalOrder(graph);
        Map<String, Integer> rank = new HashMap<>();
        for (int i = 0; i < order.size(); i++) rank.put(order.get(i), i);
        measurements.sort((m1, m2) -> Integer.compare(minRank(m1, rank), minRank(m2, rank)));
        return measurements;
    }

    private List<String> topologicalOrder(Graph<String, DefaultEdge> graph) {
        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String v : graph.vertexSet()) {
            if (!visited.contains(v)) topologicalSortUtil(v, visited, stack, graph);
        }
        while (!stack.isEmpty()) order.add(stack.pop());
        return order;
    }

    private void topologicalSortUtil(String v, Set<String> visited, Deque<String> stack, Graph<String, DefaultEdge> graph) {
        visited.add(v);
        for (DefaultEdge e : graph.outgoingEdgesOf(v)) {
            String target = graph.getEdgeTarget(e);
            if (!visited.contains(target)) topologicalSortUtil(target, visited, stack, graph);
        }
        stack.push(v);
    }

    private int countKnownPoints(Measurement m) { return 0; }
    private int minRank(Measurement m, Map<String, Integer> rank) { return Integer.MAX_VALUE; }

    public Set<String> findArticulationPoints(Graph<String, DefaultEdge> graph) {
        return new org.jgrapht.alg.connectivity.ArticulationPointFinder<>(graph).find();
    }

    public int getRedundancy(Graph<String, DefaultEdge> graph) {
        int vertices = graph.vertexSet().size();
        int components = getConnectedComponents(graph).size();
        return graph.edgeSet().size() - (vertices - components);
    }
}

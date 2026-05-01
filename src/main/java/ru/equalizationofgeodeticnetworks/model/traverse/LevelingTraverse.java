package ru.equalizationofgeodeticnetworks.model.traverse;

import java.util.*;

public class LevelingTraverse extends Traverse {
    private final Map<String, Double> fixedHeights = new HashMap<>();
    private final List<String> unknownNames = new ArrayList<>();
    private final List<RawHeightDiff> rawDiffs = new ArrayList<>();

    public static class RawHeightDiff {
        public String from, to;
        public double value, sigma;
    }

    public LevelingTraverse() {
        detectTraverseType();
    }

    public LevelingTraverse addFixedPoint(String name, double height) {
        fixedHeights.put(name, height);
        pointNames.add(name);
        detectTraverseType();
        return this;
    }

    @Override
    public void addFixedPoint(String name, double... values) {
        if (values.length != 1) throw new IllegalArgumentException("Требуется высота");
        addFixedPoint(name, values[0]);
    }

    @Override
    public void addUnknownPoint(String name) {
        unknownNames.add(name);
        pointNames.add(name);
        detectTraverseType();
    }

    public LevelingTraverse addHeightDifference(String from, String to, double value, double sigma) {
        RawHeightDiff rh = new RawHeightDiff();
        rh.from = from; rh.to = to; rh.value = value; rh.sigma = sigma;
        rawDiffs.add(rh);
        return this;
    }

    @Override
    protected void detectTraverseType() {
        int fixedCount = fixedHeights.size();
        if (fixedCount == 0) {
            isFreeNetwork = true;
            isOpenTraverse = true;
        } else if (fixedCount == 1) {
            isFreeNetwork = false;
            isOpenTraverse = true;
        } else {
            isFreeNetwork = false;
            isOpenTraverse = false;
        }
        hasConditions = false;
    }

    @Override
    public void computeInitialApprox() {
        if (unknownNames.isEmpty()) return;
        if (!initialApprox.isEmpty() && initialApprox.size() == unknownNames.size()) {
            approxComputed = true;
            return;
        }
        Map<String, Double> known = new HashMap<>(fixedHeights);
        for (RawHeightDiff rh : rawDiffs) {
            if (known.containsKey(rh.from) && !known.containsKey(rh.to))
                known.put(rh.to, known.get(rh.from) + rh.value);
            else if (!known.containsKey(rh.from) && known.containsKey(rh.to))
                known.put(rh.from, known.get(rh.to) - rh.value);
        }
        initialApprox.clear();
        for (String name : unknownNames) {
            Double h = known.get(name);
            if (h == null) throw new IllegalStateException("Не удалось вычислить высоту для " + name);
            initialApprox.add(h);
        }
        approxComputed = true;
    }

    public Map<String, Double> getFixedHeights() { return fixedHeights; }
    public List<String> getUnknownNames() { return unknownNames; }
    public List<RawHeightDiff> getRawDiffs() { return rawDiffs; }
}

package ru.equalizationofgeodeticnetworks.measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexMapper {

    private final Map<String, Integer> nameToIndex = new HashMap<>();
    private final List<String> unknownNames;

    public IndexMapper(List<String> unknownNames) {
        this.unknownNames = unknownNames;
        build();
    }

    private void build() {
        nameToIndex.clear();
        for (int i = 0; i < unknownNames.size(); i++) {
            nameToIndex.put(unknownNames.get(i), i * 2);
        }
    }

    public int getIndex(String name) {
        return nameToIndex.getOrDefault(name, -1);
    }

    public int getParamCount() { return unknownNames.size() * 2; }
    public List<String> getUnknownNames() { return unknownNames; }
}

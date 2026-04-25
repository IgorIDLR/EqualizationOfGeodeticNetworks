package ru.equalizationofgeodeticnetworks.processor;

import ru.equalizationofgeodeticnetworks.network.NetworkData;

public interface MeasurementProcessor {

    ProcessingState process(ProcessingState state, NetworkData.RawMeasurement measurement);
}

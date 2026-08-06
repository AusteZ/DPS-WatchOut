package player.repository;

import dtos.MeasurementValue;

import java.util.ArrayList;
import java.util.List;

public class MeasurementValueRepository {
    private final List<MeasurementValue> measurements = new ArrayList<>();

    public List<MeasurementValue> getAndClearMeasurements() {
        synchronized (measurements) {
            List<MeasurementValue> measurementValues = new ArrayList<>(measurements);
            measurements.clear();
            return measurementValues;
        }
    }

    public void addMeasurement(MeasurementValue measurement) {
        synchronized (measurements) {
            measurements.add(measurement);
        }
    }
}

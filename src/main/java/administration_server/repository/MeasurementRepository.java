package administration_server.repository;

import dtos.MeasurementListDto;
import dtos.MeasurementValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MeasurementRepository {
    private final Map<Integer, List<MeasurementValue>> measurementValuesByPlayerId = new HashMap<>();

    public void addMeasurements(MeasurementListDto measurementListDto) {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementValue> values = measurementListDto.values();
            measurementValuesByPlayerId.computeIfAbsent(measurementListDto.id(), _ -> new ArrayList<>())
                    .addAll(values);
        }
    }

    public List<MeasurementValue> getLastestMeasurements(int playerId, int count) {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementValue> measurements = measurementValuesByPlayerId.get(playerId);
            if (measurements == null) {
                return List.of();
            }

            measurements.sort(Comparator.comparingLong(MeasurementValue::timestamp));

            count = Math.min(measurements.size(), count);
            int firstIndex = measurements.size() - count;

            List<MeasurementValue> measurementSublist = measurements.subList(firstIndex, count);

            return new ArrayList<>(measurementSublist);
        }
    }

    public List<MeasurementValue> getMeasurementsBetweenTimestamps(long startTimestamp, long endTimestamp) {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementValue> measurements = measurementValuesByPlayerId.values()
                    .stream()
                    .flatMap(List::stream)
                    .sorted(Comparator.comparingLong(MeasurementValue::timestamp))
                    .toList();

            MeasurementValue startTimestampMeasurement = new MeasurementValue(startTimestamp, 0);
            int firstIndex = Collections.binarySearch(measurements, startTimestampMeasurement, Comparator.comparingLong(MeasurementValue::timestamp));

            MeasurementValue endTimestampMeasurement = new MeasurementValue(endTimestamp, 0);
            int lastIndex = Collections.binarySearch(measurements, endTimestampMeasurement, Comparator.comparingLong(MeasurementValue::timestamp));
            lastIndex = Math.min(measurements.size(), 1 + lastIndex);
            List<MeasurementValue> measurementSublist = measurements.subList(firstIndex, lastIndex);
            return new ArrayList<>(measurementSublist);
        }
    }
}

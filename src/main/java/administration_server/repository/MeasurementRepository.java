package administration_server.repository;

import administration_server.repository.dao.MeasurementDao;
import dtos.enums.MeasurementType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MeasurementRepository {
    private final Map<Integer, Map<MeasurementType, List<MeasurementDao>>> measurementsByPlayerId = new HashMap<>();

    public void addMeasurements(int playerId, MeasurementType measurementType, List<MeasurementDao> measurements) {
        Map<MeasurementType, List<MeasurementDao>> measurementsByIds;
        synchronized (measurementsByPlayerId) {
            measurementsByPlayerId
                    .computeIfAbsent(playerId, _ -> new HashMap<>())
                    .computeIfAbsent(measurementType, _ -> new ArrayList<>())
                    .addAll(measurements);
        }
    }

    public Map<MeasurementType, List<MeasurementDao>> filterLatestMeasurements(int playerId, int count) {
        synchronized (measurementsByPlayerId) {
            Map<MeasurementType, List<MeasurementDao>> measurements = measurementsByPlayerId.get(playerId);

            if (measurements == null) {
                return Map.of();
            }

            return filterLatestMeasurements(measurements, count);
        }
    }

    public Map<MeasurementType, List<MeasurementDao>> getMeasurementsBetweenTimestamps(
            long startTimestamp,
            long endTimestamp
    ) {
        synchronized (measurementsByPlayerId) {
            Map<MeasurementType, List<MeasurementDao>> result = new HashMap<>();

            for (Map<MeasurementType, List<MeasurementDao>> playerMeasurements : measurementsByPlayerId.values()) {
                for (var entry : playerMeasurements.entrySet()) {
                    List<MeasurementDao> measurements = entry.getValue()
                            .stream()
                            .filter(measurement ->
                                    measurement.timestamp() >= startTimestamp &&
                                            measurement.timestamp() <= endTimestamp
                            )
                            .toList();

                    result.computeIfAbsent(entry.getKey(), _ -> new ArrayList<>())
                            .addAll(measurements);
                }
            }

            result.values().forEach(list ->
                    list.sort(Comparator.comparingLong(MeasurementDao::timestamp))
            );

            return result;
        }
    }

    private Map<MeasurementType, List<MeasurementDao>> filterLatestMeasurements(Map<MeasurementType, List<MeasurementDao>> measurements, int count) {
        Map<MeasurementType, List<MeasurementDao>> result = new HashMap<>();

        for (var entry : measurements.entrySet()) {
            List<MeasurementDao> values = new ArrayList<>(entry.getValue());

            result.put(
                    entry.getKey(),
                    filterLatestMeasurements(count, values)
            );
        }

        return result;
    }

    private List<MeasurementDao> filterLatestMeasurements(int count, List<MeasurementDao> values) {
        values.sort(Comparator.comparingLong(MeasurementDao::timestamp));

        int actualCount = Math.min(values.size(), count);
        int firstIndex = values.size() - actualCount;

        return new ArrayList<>(values.subList(firstIndex, values.size()));
    }
}

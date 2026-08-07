package administration_server.repository;

import administration_server.repository.dao.MeasurementDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MeasurementRepository {
    private final Map<Integer, List<MeasurementDao>> measurementValuesByPlayerId = new HashMap<>();

    public void addMeasurements(int playerId, List<MeasurementDao> measurements) {
        synchronized (measurementValuesByPlayerId) {
            measurementValuesByPlayerId.computeIfAbsent(playerId, _ -> new ArrayList<>())
                    .addAll(measurements);
        }
    }

    public List<MeasurementDao> getLastestMeasurements(int playerId, int count) {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementDao> measurements = measurementValuesByPlayerId.get(playerId);
            if (measurements == null) {
                return List.of();
            }

            measurements.sort(Comparator.comparingLong(MeasurementDao::timestamp));

            count = Math.min(measurements.size(), count);
            int firstIndex = measurements.size() - count;

            List<MeasurementDao> measurementSublist = measurements.subList(firstIndex, count);

            return new ArrayList<>(measurementSublist);
        }
    }

    public List<MeasurementDao> getMeasurementsBetweenTimestamps(long startTimestamp, long endTimestamp) {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementDao> measurements = measurementValuesByPlayerId.values()
                    .stream()
                    .flatMap(List::stream)
                    .sorted(Comparator.comparingLong(MeasurementDao::timestamp))
                    .toList();

            MeasurementDao startTimestampMeasurement = new MeasurementDao(startTimestamp, 0);
            int firstIndex = Collections.binarySearch(measurements, startTimestampMeasurement, Comparator.comparingLong(MeasurementDao::timestamp));

            MeasurementDao endTimestampMeasurement = new MeasurementDao(endTimestamp, 0);
            int lastIndex = Collections.binarySearch(measurements, endTimestampMeasurement, Comparator.comparingLong(MeasurementDao::timestamp));
            lastIndex = Math.min(measurements.size(), 1 + lastIndex);
            List<MeasurementDao> measurementSublist = measurements.subList(firstIndex, lastIndex);
            return new ArrayList<>(measurementSublist);
        }
    }
}

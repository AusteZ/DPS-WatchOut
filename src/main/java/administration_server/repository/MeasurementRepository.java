package administration_server.repository;

import Exceptions.UnitializedPlayerException;
import dtos.MeasurementListDto;
import dtos.MeasurementValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MeasurementRepository {
    private final HashMap<Integer, List<MeasurementValue>> measurementValuesByPlayerId = new HashMap<>();

    public void addMeasurements(MeasurementListDto measurementListDto) {
        synchronized (measurementValuesByPlayerId) {
            if (!measurementValuesByPlayerId.containsKey(measurementListDto.id())) {
                measurementValuesByPlayerId.put(measurementListDto.id(), new ArrayList<>());
            }

            List<MeasurementValue> values = measurementListDto.values();

            measurementValuesByPlayerId.get(measurementListDto.id()).addAll(values);
        }
    }

    public List<MeasurementValue> getLastestMeasurements(int playerId, int count) throws UnitializedPlayerException {
        synchronized (measurementValuesByPlayerId) {
            List<MeasurementValue> measurements = measurementValuesByPlayerId.get(playerId);
            if (measurements == null) {
                throw new UnitializedPlayerException("No such player id found.");
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

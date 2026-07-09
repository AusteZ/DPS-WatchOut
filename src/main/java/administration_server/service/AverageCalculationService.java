package administration_server.service;

import Exceptions.NoDataRecordedException;
import Exceptions.UnitializedPlayerException;
import administration_server.storage.MeasurementStorage;
import dtos.MeasurementValue;

import java.util.List;

public final class AverageCalculationService {

    public double calculateMeasurementAverageBetweenTimestamps(long startTimestamp, long endTimestamp) {
        List<MeasurementValue> measurements = MeasurementStorage.getMeasurementsBetweenTimestamps(startTimestamp, endTimestamp);

        return calculateAverage(measurements);
    }

    public double calculateLatestMeasurementAverage(int playerId, int count) throws UnitializedPlayerException {
        List<MeasurementValue> measurements = MeasurementStorage.getLastestMeasurements(playerId, count);

        return calculateAverage(measurements);
    }

    private double calculateAverage(List<MeasurementValue> measurements) {
        if(measurements.isEmpty()){
            throw new NoDataRecordedException("No measurements found at this time.");
        }

        double sum = 0.0d;
        for(MeasurementValue measurementValue : measurements) {
            sum += measurementValue.value();
        }

        return sum / measurements.size();
    }
}

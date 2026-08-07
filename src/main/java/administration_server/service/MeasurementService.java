package administration_server.service;

import Exceptions.NoDataRecordedException;
import Exceptions.UninitializedPlayerException;
import administration_server.repository.MeasurementRepository;
import dtos.MeasurementListDto;
import dtos.MeasurementValue;

import java.util.List;

public final class MeasurementService {
    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public void addMeasurements(MeasurementListDto measurementListDto) {
        measurementRepository.addMeasurements(measurementListDto);
    }

    public double calculateMeasurementAverageBetweenTimestamps(long startTimestamp, long endTimestamp) {
        List<MeasurementValue> measurements = measurementRepository.getMeasurementsBetweenTimestamps(startTimestamp, endTimestamp);

        return calculateAverage(measurements);
    }

    public double calculateLatestMeasurementAverage(int playerId, int count) throws UninitializedPlayerException {
        List<MeasurementValue> measurements = measurementRepository.getLastestMeasurements(playerId, count);

        return calculateAverage(measurements);
    }

    private double calculateAverage(List<MeasurementValue> measurements) {
        if (measurements.isEmpty()) {
            throw new NoDataRecordedException("No measurements found at this time.");
        }

        double sum = 0.0d;
        for (MeasurementValue measurementValue : measurements) {
            sum += measurementValue.value();
        }

        return sum / measurements.size();
    }
}

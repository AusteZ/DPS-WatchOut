package administration_server.service;

import administration_server.exception.NotFoundException;
import administration_server.repository.MeasurementRepository;
import dtos.MeasurementListDto;
import dtos.MeasurementValue;
import jakarta.validation.ValidationException;

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
        if (startTimestamp > endTimestamp) {
            throw new ValidationException("Start timestamp cannot be after end timestamp");
        }

        List<MeasurementValue> measurements = measurementRepository.getMeasurementsBetweenTimestamps(startTimestamp, endTimestamp);

        if (measurements.isEmpty()) {
            throw new NotFoundException("No measurements found. [startTimestamp=%d, endTimestamp=%d]".formatted(startTimestamp, endTimestamp));
        }

        return calculateAverage(measurements);
    }

    public double calculateLatestMeasurementAverage(int playerId, int count) {
        if (count <= 0) {
            throw new ValidationException("Count cannot be zero or less");
        }

        List<MeasurementValue> measurements = measurementRepository.getLastestMeasurements(playerId, count);

        if (measurements.isEmpty()) {
            throw new NotFoundException("No measurements found. [playerId=%s]".formatted(playerId));
        }

        return calculateAverage(measurements);
    }

    private double calculateAverage(List<MeasurementValue> measurements) {
        double sum = 0.0d;
        for (MeasurementValue measurementValue : measurements) {
            sum += measurementValue.value();
        }

        return sum / measurements.size();
    }
}

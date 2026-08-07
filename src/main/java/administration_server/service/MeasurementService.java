package administration_server.service;

import administration_server.exception.NotFoundException;
import administration_server.mapper.MeasurementMapper;
import administration_server.repository.MeasurementRepository;
import administration_server.repository.dao.MeasurementDao;
import dtos.MeasurementListDto;
import jakarta.validation.ValidationException;

import java.util.List;

public final class MeasurementService {
    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public void addMeasurements(MeasurementListDto measurementListDto) {
        List<MeasurementDao> measurementDaoList = MeasurementMapper.toDao(measurementListDto.values());
        measurementRepository.addMeasurements(measurementListDto.id(), measurementDaoList);
    }

    public double calculateMeasurementAverageBetweenTimestamps(long startTimestamp, long endTimestamp) {
        if (startTimestamp > endTimestamp) {
            throw new ValidationException("Start timestamp cannot be after end timestamp");
        }

        List<MeasurementDao> measurements = measurementRepository.getMeasurementsBetweenTimestamps(startTimestamp, endTimestamp);

        if (measurements.isEmpty()) {
            throw new NotFoundException("No measurements found.");
        }

        return calculateAverage(measurements);
    }

    public double calculateLatestMeasurementAverage(int playerId, int count) {
        if (count <= 0) {
            throw new ValidationException("Count cannot be zero or less");
        }

        List<MeasurementDao> measurements = measurementRepository.filterLatestMeasurements(playerId, count);

        if (measurements.isEmpty()) {
            throw new NotFoundException("No measurements found.");
        }

        return calculateAverage(measurements);
    }

    private double calculateAverage(List<MeasurementDao> measurements) {
        double sum = 0.0d;
        for (MeasurementDao measurementValue : measurements) {
            sum += measurementValue.value();
        }

        return sum / measurements.size();
    }
}

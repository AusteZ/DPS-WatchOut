package administration_server.service;

import administration_server.exception.NotFoundException;
import administration_server.mapper.MeasurementMapper;
import administration_server.repository.MeasurementRepository;
import administration_server.repository.dao.MeasurementDao;
import dtos.MeasurementAverageDto;
import dtos.MeasurementListDto;
import dtos.enums.MeasurementType;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MeasurementService {
    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public void addMeasurements(MeasurementListDto measurementListDto) {
        List<MeasurementDao> measurementDaoList = MeasurementMapper.toDao(measurementListDto.values());
        measurementRepository.addMeasurements(measurementListDto.id(), measurementListDto.measurementType(), measurementDaoList);
    }

    public List<MeasurementAverageDto> calculateMeasurementAveragesBetweenTimestamps(long startTimestamp, long endTimestamp) {
        if (startTimestamp > endTimestamp) {
            throw new ValidationException("Start timestamp cannot be after end timestamp");
        }

        Map<MeasurementType, List<MeasurementDao>> measurements = measurementRepository.getMeasurementsBetweenTimestamps(startTimestamp, endTimestamp);

        return getMeasurementAverageDtoList(measurements);
    }

    public List<MeasurementAverageDto> calculateLatestMeasurementAverages(int playerId, int count) {
        if (count <= 0) {
            throw new ValidationException("Count cannot be zero or less");
        }

        Map<MeasurementType, List<MeasurementDao>> measurements = measurementRepository.filterLatestMeasurements(playerId, count);

        return getMeasurementAverageDtoList(measurements);
    }

    private List<MeasurementAverageDto> getMeasurementAverageDtoList(Map<MeasurementType, List<MeasurementDao>> measurements) {
        if (measurements.isEmpty()) {
            throw new NotFoundException("No measurements found.");
        }

        List<MeasurementAverageDto> averages = new ArrayList<>();
        for (var entry : measurements.entrySet()) {
            double average = calculateAverage(entry.getValue());
            averages.add(new MeasurementAverageDto(entry.getKey(), average));
        }

        return averages;
    }

    private double calculateAverage(List<MeasurementDao> measurements) {
        double sum = 0.0d;
        for (MeasurementDao measurementValue : measurements) {
            sum += measurementValue.value();
        }

        return sum / measurements.size();
    }
}

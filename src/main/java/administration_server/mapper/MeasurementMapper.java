package administration_server.mapper;

import administration_server.repository.dao.MeasurementDao;
import dtos.MeasurementValue;

import java.util.List;

public final class MeasurementMapper {
    private MeasurementMapper() {
        throw new IllegalStateException("Mapper class");
    }

    public static List<MeasurementDao> toDao(List<MeasurementValue> measurements) {
        return measurements
                .stream()
                .map(MeasurementMapper::toDao)
                .toList();

    }

    public static MeasurementDao toDao(MeasurementValue measurementValue) {
        return new MeasurementDao(measurementValue.timestamp(), measurementValue.value());
    }
}

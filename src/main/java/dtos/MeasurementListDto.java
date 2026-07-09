package dtos;


import java.util.List;

public record MeasurementListDto(int id, long timestamp, List<MeasurementValue> values) {
}

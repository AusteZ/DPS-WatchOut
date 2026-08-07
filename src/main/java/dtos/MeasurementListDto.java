package dtos;


import dtos.enums.MeasurementType;

import java.util.List;

public record MeasurementListDto(int id, long timestamp, MeasurementType measurementType, List<MeasurementValue> values) {
}

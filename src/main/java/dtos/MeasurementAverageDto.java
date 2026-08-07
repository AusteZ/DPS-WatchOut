package dtos;

import dtos.enums.MeasurementType;

public record MeasurementAverageDto(MeasurementType type, double average) {
}

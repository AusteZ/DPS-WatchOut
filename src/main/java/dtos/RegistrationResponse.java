package dtos;

import java.util.List;

public record RegistrationResponse(List<PlayerInfo> players, CoordinatesDto assignedCoordinates) {
}

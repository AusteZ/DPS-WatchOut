package player.service.simulation;

import Simulators.Buffer;
import Simulators.HRSimulator;
import player.client.AdminServerClient;
import player.repository.MeasurementValueRepository;
import player.repository.dao.Player;

public final class HeartRateSimulationService {
    private final HRCollectValuesThread hrCollectValues;
    private final HRSendToServerThread hrSendToServerThread;
    private final HRSimulator hrSimulator;

    public HeartRateSimulationService(Player localPlayer, Buffer buffer, AdminServerClient adminServerClient, MeasurementValueRepository measurementValueRepository) {
        hrCollectValues = new HRCollectValuesThread(buffer, measurementValueRepository);
        hrSendToServerThread = new HRSendToServerThread(localPlayer, adminServerClient, measurementValueRepository);
        hrSimulator = new HRSimulator(buffer);
    }

    public void startHeartRateSimulation() {
        hrSendToServerThread.start();
        hrSimulator.start();
        hrCollectValues.start();
    }
}

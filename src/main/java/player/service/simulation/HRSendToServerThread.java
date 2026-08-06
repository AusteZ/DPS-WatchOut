package player.service.simulation;

import dtos.MeasurementListDto;
import dtos.MeasurementValue;
import player.client.AdminServerClient;
import player.repository.MeasurementValueRepository;
import player.repository.dao.Player;

import java.io.IOException;
import java.util.List;

final class HRSendToServerThread extends Thread {
    private final Player localPlayer;
    private final AdminServerClient adminServerClient;
    private final MeasurementValueRepository measurementValueRepository;

    public HRSendToServerThread(Player localPlayer, AdminServerClient client, MeasurementValueRepository measurementValueRepository) {
        this.localPlayer = localPlayer;
        this.adminServerClient = client;
        this.measurementValueRepository = measurementValueRepository;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10 * 1000);
                MeasurementListDto measurementListDto = getMeasurements();
                adminServerClient.postMeasurements(measurementListDto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("HR sending thread was interrupted.");
                break;

            } catch (IOException e) {
                System.out.println("Failed to send heart rate measurements: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("Unexpected error while sending heart rate measurements: " + e.getMessage());
            }
        }
    }

    private MeasurementListDto getMeasurements() {
        List<MeasurementValue> list = measurementValueRepository.getAndClearMeasurements();

        if (list.isEmpty()) {
            throw new RuntimeException("No measurements found");
        }

        return new MeasurementListDto(
                localPlayer.playerId(),
                System.currentTimeMillis(),
                list
        );
    }
}
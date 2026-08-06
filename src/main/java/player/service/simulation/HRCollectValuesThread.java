package player.service.simulation;

import Simulators.Buffer;
import Simulators.Measurement;
import dtos.MeasurementValue;
import player.repository.MeasurementValueRepository;

import java.util.List;

final class HRCollectValuesThread extends Thread {
    private final Buffer monitor;
    private final MeasurementValueRepository measurementValueRepository;

    public HRCollectValuesThread(Buffer monitor, MeasurementValueRepository measurementValueRepository) {
        this.monitor = monitor;
        this.measurementValueRepository = measurementValueRepository;
    }

    public void run() {

        while (true) {
            double average = 0.0d;
            List<Measurement> measurements = monitor.readAllAndClean();
            for (Measurement measurement : measurements) {
                average += measurement.getValue();
            }
            average /= 8.0d;
            measurementValueRepository.addMeasurement(new MeasurementValue(System.currentTimeMillis(), average));
        }
    }
}

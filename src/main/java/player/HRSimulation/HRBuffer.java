package player.HRSimulation;

import Simulators.Buffer;
import Simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class HRBuffer implements Buffer {
    ArrayList<Measurement> measurements = new ArrayList<>();
    int overlap = -4;
    public void addMeasurement(Measurement m){
        synchronized(measurements) {
            overlap++;
            measurements.add(m);
            measurements.notify();
        }
    }

    public List<Measurement> readAllAndClean() {
        synchronized(measurements) {
            while(overlap != 4) {
                try {
                    measurements.wait();
                } catch (InterruptedException e) {}
            }

            List<Measurement> measure = new ArrayList<>(measurements);
            for(; 0 < overlap; --overlap) {
                measurements.remove(0);
            }
            
            return measure;
        }
    }
}

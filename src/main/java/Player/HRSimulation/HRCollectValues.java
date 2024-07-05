package Player.HRSimulation;

import Beans.MeasurementValue;
import Simulators.Buffer;
import Simulators.HRSimulator;
import Simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class HRCollectValues extends Thread {
    private static HRBuffer monitor = new HRBuffer();
    private static ArrayList<MeasurementValue> lastMeasurements = new ArrayList<>();
    public static Buffer returnMonitor(){
        return monitor;
    }
    public static List<MeasurementValue> getLastMeasurements(){
        synchronized(lastMeasurements) {
            ArrayList<MeasurementValue> measurements = new ArrayList<>(lastMeasurements);
            lastMeasurements.clear();
            return measurements;
        }
    }
    public void run(){
        (new HRSendToServerThread()).start();
        (new HRSimulator(HRCollectValues.returnMonitor())).start();
        while(true) {
            double average = 0.0d;
            List<Measurement> measurements = HRCollectValues.monitor.readAllAndClean();
            for(Measurement measurement : measurements) {
                average += measurement.getValue();
            }
            average /= 8.0d;
            synchronized(lastMeasurements){
                lastMeasurements.add(new MeasurementValue(System.currentTimeMillis(), average));
            }
            
        }
    }
}

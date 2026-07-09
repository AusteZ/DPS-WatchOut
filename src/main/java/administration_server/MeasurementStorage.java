package administration_server;

import dtos.MeasurementList;
import dtos.MeasurementValue;
import Exceptions.UnitializedPlayerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeasurementStorage {
    static HashMap<Integer, ArrayList<MeasurementList>> storage = new HashMap<Integer, ArrayList<MeasurementList>>();
    
    
    public static void addMeasurements(MeasurementList values){
        synchronized(storage){
            if(!storage.containsKey(values.getId())){
                storage.put(values.getId(), new ArrayList<MeasurementList>());
            }
            storage.get(values.getId()).add(values);
        }
    }
    public static double getLastNMeasurements(int id, int n) throws UnitializedPlayerException {
        synchronized(storage) {
            if(!storage.containsKey(id)) {
                throw new UnitializedPlayerException("No such player id found.");
            }
            double average = 0.0d;
            int counter = 0;
            for(int i = storage.get(id).size() - 1; i >= 0 && counter < n; i--) {
                List<MeasurementValue> values = storage.get(id).get(i).getValues();
                for(int j = values.size() - 1; j >= 0 && counter < n; j--) {
                    average += values.get(j).getValue();
                    counter++;
                }
            }
            return counter > 0 ? average / counter : 0.0;
        }
    }
    public static double getMeasurementsBetweenTimestamps(long timestamp1, long timestamp2){
        synchronized(storage) {
            ArrayList<ArrayList<MeasurementList>> values = new ArrayList<>(storage.values());
            double average = 0.0d;
            int counter = 0;
            for(ArrayList<MeasurementList> m : values) {
                for(MeasurementList ml : m) {
                    if(ml.getTimestamp() >= timestamp1 && ml.getTimestamp() <= timestamp2) {
                        for(MeasurementValue mv : ml.getValues()) {
                            average += mv.getValue();
                            counter++;
                        }
                    }
                }
            }
            return counter > 0 ? average / counter : -1.0;
        }
    }
    
}

package Beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurementList {
    private int id;
    private long timestamp;
    private List<MeasurementValue> values = new ArrayList<MeasurementValue>();
    
    public MeasurementList(int id, long timestamp, List<MeasurementValue> values) {
        this.id = id;
        this.timestamp = timestamp;
        this.values = values;
    }
    public MeasurementList(){}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public List<MeasurementValue> getValues() {
        return values;
    }
    public void setValues(ArrayList<MeasurementValue> values) {
        this.values = values;
    }

    //List<Measurement> measurements;
    //public synchronized List<Measurement> getPlayersList() {
        //return new ArrayList<>(Measurement);
    //}
}

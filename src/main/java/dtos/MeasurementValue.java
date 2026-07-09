package dtos;

public class MeasurementValue {
    private long timestamp;
    private double value;
    public MeasurementValue(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
    public MeasurementValue(){}
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}

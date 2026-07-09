package dtos;

public class Timestamps {
    private long timestamp1;
    private long timestamp2;
    public Timestamps(long timestamp1, long timestamp2) {
        this.timestamp1 = timestamp1;
        this.timestamp2 = timestamp2;
    }
    public Timestamps() {}
    public long getTimestamp1() {
        return timestamp1;
    }
    public void setTimestamp1(long timestamp1) {
        this.timestamp1 = timestamp1;
    }
    public long getTimestamp2() {
        return timestamp2;
    }
    public void setTimestamp2(long timestamp2) {
        this.timestamp2 = timestamp2;
    }
}

package Beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Average {
    private double average;
    public Average() {}
    public void setAverage(double average) {
        this.average = average;
    }
    public double getAverage() {
        return average;
    }
}

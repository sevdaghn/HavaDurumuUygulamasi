package com.example.havadurumuuygulamasi;

public class WeatherResult {
    public final double currentTemp;
    public final int currentCode;
    public final String[] times;
    public final double[] temps;
    public final int[] codes;


    public WeatherResult(double currentTemp, int currentCode, String[] times, double[] temps, int[] codes) {
        this.currentTemp = currentTemp;
        this.currentCode = currentCode;
        this.times = times;
        this.temps = temps;
        this.codes = codes;
    }
}

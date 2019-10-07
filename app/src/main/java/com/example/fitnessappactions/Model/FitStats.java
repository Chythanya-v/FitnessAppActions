package com.example.fitnessappactions.Model;

public class FitStats {
   public int TotalCount;
   public double totalDistanceMeters;
  public  long totalDurationMs;

    public FitStats(int totalCount, double totalDistanceMeters, long totalDurationMs) {
        TotalCount = totalCount;
        this.totalDistanceMeters = totalDistanceMeters;
        this.totalDurationMs = totalDurationMs;
    }
}

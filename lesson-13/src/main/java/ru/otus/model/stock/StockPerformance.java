package ru.otus.model.stock;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.ArrayDeque;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class StockPerformance {
    private static final int MAX_LOOK_BACK = 5;

    Long lastUpdateSent;
    @Builder.Default
    double currentPrice = 0.0;
    @Builder.Default
    double priceDifferential = 0.0;
    @Builder.Default
    double shareDifferential = 0.0;
    @Builder.Default
    int currentShareVolume = 0;
    @Builder.Default
    double currentAveragePrice = Double.MIN_VALUE;
    @Builder.Default
    double currentAverageVolume = Double.MIN_VALUE;
    @Builder.Default
    ArrayDeque<Double> shareVolumeLookback = new ArrayDeque<>(MAX_LOOK_BACK);
    @Builder.Default
    ArrayDeque<Double> sharePriceLookback = new ArrayDeque<>(MAX_LOOK_BACK);


    public static class StockPerformanceBuilder {

        public StockPerformanceBuilder updatePriceStats(double currentPrice) {
            this.currentPrice(currentPrice);
            this.priceDifferential(calculateDifferentialFromAverage(currentPrice, this.currentAveragePrice$value));
            this.currentAveragePrice(calculateNewAverage(currentPrice, currentAveragePrice$value, sharePriceLookback$value));
            return this;
        }

        public StockPerformanceBuilder updateVolumeStats(int currentShareVolume) {
            this.currentShareVolume(currentShareVolume);
            this.shareDifferential(calculateDifferentialFromAverage(currentShareVolume, currentAverageVolume$value));
            this.currentAverageVolume(calculateNewAverage(currentShareVolume, currentAverageVolume$value, shareVolumeLookback$value));
            return this;
        }

        private static double calculateDifferentialFromAverage(double value, double average) {
            return average != Double.MIN_VALUE ? ((value / average) - 1) * 100.0 : 0.0;
        }

        private static double calculateNewAverage(double newValue, double currentAverage, ArrayDeque<Double> deque) {
            if (deque.size() < MAX_LOOK_BACK) {
                deque.add(newValue);

                if (deque.size() == MAX_LOOK_BACK) {
                    currentAverage = deque.stream().reduce(0.0, Double::sum) / MAX_LOOK_BACK;
                }

            } else {
                double oldestValue = deque.poll();
                deque.add(newValue);
                currentAverage = (currentAverage + (newValue / MAX_LOOK_BACK)) - oldestValue / MAX_LOOK_BACK;
            }
            return currentAverage;
        }
    }
}

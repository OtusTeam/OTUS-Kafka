package ru.otus.model.stock;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@AllArgsConstructor
public class ShareVolume {

    String symbol;
    int shares;
    String industry;

    public static ShareVolume sum(ShareVolume csv1, ShareVolume csv2) {
        var builder = csv1.toBuilder();
        builder.shares = csv1.shares + csv2.shares;
        return builder.build();
    }

    public static ShareVolumeBuilder builder() {
        return new ShareVolumeBuilder();
    }

    public static ShareVolumeBuilder newBuilder(StockTransaction stockTransaction) {
        var builder = builder();
        builder.symbol = stockTransaction.getSymbol();
        builder.shares = stockTransaction.getShares();
        builder.industry = stockTransaction.getIndustry();
        return builder;
    }
}

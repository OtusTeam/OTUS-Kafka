package ru.otus.model.stock;


import lombok.Data;
import lombok.SneakyThrows;
import lombok.Value;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

@Data
public  class PublicTradedCompany {
    private double volatility;
    private double lastSold;
    private String symbol;
    private String name;
    private String sector;
    private String industry;
    private double price;
    private NumberFormat formatter = new DecimalFormat("0.00");

    public PublicTradedCompany(double volatility, double lastSold, String symbol, String name, String sector, String industry) {
        this.volatility = volatility;
        this.lastSold = lastSold;
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.sector = sector;
        this.industry = industry;
        this.price = lastSold;
    }

    @SneakyThrows
    public double updateStockPrice() {
        double min = (price * -volatility);
        double max = (price * volatility);
        double randomNum = ThreadLocalRandom.current().nextDouble(min, max + 1);
        price = formatter.parse(formatter.format(price + randomNum)).doubleValue();;
        return price;
    }

}
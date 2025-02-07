package ru.otus.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.model.stock.PublicTradedCompany;
import ru.otus.model.stock.StockTickerData;

import java.util.List;

import static ru.otus.utils.Utils.STOCK_TICKER_STREAM_TOPIC;
import static ru.otus.utils.Utils.STOCK_TICKER_TABLE_TOPIC;

public class StockProducer extends AbstractProducer {
    private final int numberCompanies;
    private final int numberIterations;

    public StockProducer(int numberCompanies, int numberIterations) {
        super("stock-topic", Utils.producerConfig);

        this.numberCompanies = numberCompanies;
        this.numberIterations = numberIterations;
    }

    public StockProducer() {
        this(50, 10);
    }

    @Override
    protected void doSend(KafkaProducer<String, String> producer) throws Exception {
        int counter = 0;
        List<PublicTradedCompany> publicTradedCompanyList = DataGenerator.stockTicker(numberCompanies);

        while (counter++ < numberIterations && !Thread.interrupted()) {
            for (PublicTradedCompany company : publicTradedCompanyList) {
                String value = convertToJson(StockTickerData.builder().price(company.getPrice()).symbol(company.getSymbol()).build());

                ProducerRecord<String, String> record = new ProducerRecord<>(STOCK_TICKER_STREAM_TOPIC, company.getSymbol(), value);
                producer.send(record);

                record = new ProducerRecord<>(STOCK_TICKER_TABLE_TOPIC, company.getSymbol(), value);
                producer.send(record);

                company.updateStockPrice();
            }
            producer.flush();
            Utils.log.info("Stock updates sent");
            Thread.sleep(1000);
        }
    }
}

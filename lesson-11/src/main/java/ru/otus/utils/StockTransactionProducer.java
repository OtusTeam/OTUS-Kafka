package ru.otus.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.model.stock.PublicTradedCompany;
import ru.otus.model.stock.StockTransaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.otus.utils.Utils.CLIENTS;
import static ru.otus.utils.Utils.COMPANIES;
import static ru.otus.utils.Utils.FINANCIAL_NEWS;
import static ru.otus.utils.Utils.STOCK_TRANSACTIONS_TOPIC;

public class StockTransactionProducer extends AbstractProducer {
    private final int numberIterations;
    private final int numberTradedCompanies;
    private final int numberCustomers;
    private final boolean populateGlobalTables;

    public StockTransactionProducer(int numberIterations, int numberTradedCompanies, int numberCustomers, boolean populateGlobalTables) {
        super("stock-transactions", Utils.producerConfig);

        this.numberIterations = numberIterations;
        this.numberTradedCompanies = numberTradedCompanies;
        this.numberCustomers = numberCustomers;
        this.populateGlobalTables = populateGlobalTables;
    }

    @Override
    protected void doSend(KafkaProducer<String, String> producer) throws Exception {
        List<PublicTradedCompany> companies = DataGenerator.generatePublicTradedCompanies(numberTradedCompanies);
        List<DataGenerator.Customer> customers = DataGenerator.generateCustomers(numberCustomers);

        if (populateGlobalTables) {
            populateCompaniesGlobalKTable(companies, producer);
            populateCustomersGlobalKTable(customers, producer);
        }

        publishFinancialNews(companies, producer);

        int counter = 0;
        while (counter++ < numberIterations && !Thread.interrupted()) {
            List<StockTransaction> transactions = DataGenerator.generateStockTransactions(customers, companies, 50);
            for (var transaction : transactions) {
                ProducerRecord<String, String> record = new ProducerRecord<>(STOCK_TRANSACTIONS_TOPIC, null, convertToJson(transaction));
                producer.send(record);
            }
            producer.flush();
            Utils.log.info("Stock Transactions Batch Sent");

            Thread.sleep(100);
        }
        Utils.log.info("Done generating stock data");
    }

    private void publishFinancialNews(List<PublicTradedCompany> companies, KafkaProducer<String, String> producer) throws Exception {
        Set<String> industrySet = new HashSet<>();
        for (PublicTradedCompany company : companies) {
            industrySet.add(company.getIndustry());
        }
        List<String> news = DataGenerator.generateFinancialNews();
        int counter = 0;
        for (String industry : industrySet) {
            ProducerRecord<String, String> record = new ProducerRecord<>(FINANCIAL_NEWS, industry, news.get(counter++));
            producer.send(record);
        }
        producer.flush();
        Utils.log.info("Financial news sent");
    }

    private void populateCompaniesGlobalKTable(List<PublicTradedCompany> companies, KafkaProducer<String, String> producer) {
        for (PublicTradedCompany company : companies) {
            ProducerRecord<String, String> record = new ProducerRecord<>(COMPANIES, company.getSymbol(), company.getName());
            producer.send(record);
        }
        producer.flush();
    }

    private static void populateCustomersGlobalKTable(List<DataGenerator.Customer> customers, KafkaProducer<String, String> producer) {
        for (DataGenerator.Customer customer : customers) {
            String customerName = customer.getLastName() + ", " + customer.getFirstName();
            ProducerRecord<String, String> record = new ProducerRecord<>(CLIENTS, customer.getCustomerId(), customerName);
            producer.send(record);
        }
        producer.flush();
    }
}

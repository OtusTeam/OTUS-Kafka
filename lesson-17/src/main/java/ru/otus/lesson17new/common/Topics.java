package ru.otus.lesson17new.common;

public final class Topics {
    public static final String ORDER_CREATED = "lesson17.order.created";
    public static final String PAYMENT_AUTHORIZED = "lesson17.payment.authorized";
    public static final String PAYMENT_REJECTED = "lesson17.payment.rejected";
    public static final String INVENTORY_RESERVED = "lesson17.inventory.reserved";
    public static final String INVENTORY_REJECTED = "lesson17.inventory.rejected";
    public static final String ORDER_CONFIRMED = "lesson17.order.confirmed";
    public static final String ORDER_CANCELLED = "lesson17.order.cancelled";
    public static final String ORDER_HIGH_VALUE = "lesson17.order.high-value";
    public static final String ORDER_REGULAR = "lesson17.order.regular";
    public static final String ORDER_CREATED_DLT = ORDER_CREATED + ".dlt";

    private Topics() {
    }
}

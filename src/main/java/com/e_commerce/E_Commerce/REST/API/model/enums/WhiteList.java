package com.e_commerce.E_Commerce.REST.API.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum WhiteList {

    // ======= PRODUCT FIELDS ========
    PRODUCT_ID("id", "product_id"),

    PRODUCT_NAME("Name", "product_name"),
    PRODUCT_PRICE("Price", "product_price"),
    PRODUCT_CATEGORY("category",   "category"),

    // ======= ORDER FIELDS =========
    ORDER_STATUS("orderStatus" , "order_status"),
//    ORDER_ID("orderid", "order_id"),

    // ====== CUSTOMER FIELDS ======
//    CUSTOMER_ID("id", "customer_id"),
    FIRST_NAME("firstName" , "first_name"),
    LAST_NAME("lastName" , "last_name");


    private final String apiField;
    private final String databaseColumn;

    WhiteList(String apiField , String databaseColumn)
    {
        this.apiField = apiField;
        this.databaseColumn = databaseColumn;
    }

    private static final Map<String , WhiteList> API_FIELDS = Arrays.stream(values())
            .collect(Collectors.toMap(
                    WhiteList::getApiField,
                    p -> p,
                    (p1 , p2) -> p1
            ));

    public static boolean isValid(String apiField)
    {
        return apiField != null && API_FIELDS.containsKey(apiField.toLowerCase());
    }

    public static List<String> getAllowedFields()
    {
        return Arrays.stream(values())
                .map(WhiteList::getApiField)
                .toList();
    }
}

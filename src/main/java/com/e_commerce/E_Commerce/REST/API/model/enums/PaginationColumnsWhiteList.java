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

    PRODUCT_NAME("name", "product_name"),
    PRODUCT_PRICE("price", "product_price"),
    PRODUCT_CATEGORY("category",   "category"),

    // ======= ORDER FIELDS =========
    ORDER_STATUS("orderstatus" , "order_status"),


    FIRST_NAME("firstname" , "first_name"),
    LAST_NAME("lastname" , "last_name");


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

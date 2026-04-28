package com.productionPractice.level2.enums;

import org.springframework.data.domain.Sort;

public enum ProductSortType {
    NAME_ASC("productName", Sort.Direction.ASC),
    NAME_DESC("productName",Sort.Direction.DESC),
    PRICE_ASC("price",Sort.Direction.ASC),
    PRICE_DESC("price",Sort.Direction.DESC),
    NEWEST("createdAt", Sort.Direction.DESC);

    private final String field;
    private final Sort.Direction direction;

    ProductSortType(String field, Sort.Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public Sort toSort(){
        return Sort.by(direction,field);
    }

    public static ProductSortType from(String value){
        if(value==null) return NAME_ASC;

        return switch (value.toUpperCase()) {
            case "PRICE_ASC" -> PRICE_ASC;
            case "PRICE_DESC" -> PRICE_DESC;
            case "NEWEST" -> NEWEST;
            case "NAME_DESC" -> NAME_DESC;
            default -> NAME_ASC;
        };

    }
}

package com.productionPractice.level2.service.helper;


import org.springframework.stereotype.Component;

@Component
public class CommonHelper {

    public  String normalize(String name){

        return name.trim().replaceAll("\\s+"," ");
    }

    public boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}

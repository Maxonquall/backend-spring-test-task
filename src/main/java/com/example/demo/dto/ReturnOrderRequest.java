package com.example.demo.dto;


//DTO для returnOrder, чтобы избежать проблемы, если формат JSON запроса будет не соответствовать.

public class ReturnOrderRequest {

    private Long returnedProductId;

    public Long getReturnedProductId() {
        return returnedProductId;
    }

    public void setReturnedProductId(Long returnedProductId) {
        this.returnedProductId = returnedProductId;
    }
}

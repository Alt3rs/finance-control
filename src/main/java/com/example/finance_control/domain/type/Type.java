package com.example.finance_control.domain.type;

public enum Type {
    REVENUE("revenue"),
    EXPENSE("expense");

    private String code;

    Type(String code) {
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
}

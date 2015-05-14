package com.jok.archwizacja_sms;


public class SmsData {
    private int app_sms_id;
    private int thread_id;
    private int type;
    private int date;
    private String address;
    private String body;

    public SmsData(int app_sms_id, int thread_id, int type, int date, String address, String body) {
        this.app_sms_id = app_sms_id;
        this.thread_id = thread_id;
        this.type = type;
        this.date = date;
        this.address = address;
        this.body = body;
    }

    public int getApp_sms_id() {
        return app_sms_id;
    }

    public int getThread_id() {
        return thread_id;
    }

    public int getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }
}


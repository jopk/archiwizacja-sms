package com.jok.archwizacja_sms;


class ContactData {
    private int app_contact_id;
    private int contact_id;
    private String number;
    private String name;

    ContactData(int app_contact_id, int contact_id, String number, String name) {
        this.app_contact_id = app_contact_id;
        this.contact_id = contact_id;
        this.number = number;
        this.name = name;
    }


    public int getApp_contact_id() {
        return app_contact_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

}

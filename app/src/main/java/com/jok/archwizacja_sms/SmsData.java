package com.jok.archwizacja_sms;


class SmsData {
    private int app_sms_id;
    private int _id;
    private int thread_id;
    private int m_size;
    private int person;
    private int date;
    private int date_sent;
    private int protocol;
    private int read;
    private int status;
    private int type;
    private int reply_path_present;
    private int locked;
    private int sim_id;
    private int error_code;
    private int seen;
    private int star;
    private int pri;
    private String address;
    private String body;
    private String service_center;

    SmsData(int app_sms_id, int _id, int thread_id, int m_size, int person, int date, int date_sent, int protocol, int read, int status, int type, int reply_path_present, int locked, int sim_id, int error_code, int seen, int star, int pri, String address, String body, String service_center) {
        this.app_sms_id = app_sms_id;
        this._id = _id;
        this.thread_id = thread_id;
        this.m_size = m_size;
        this.person = person;
        this.date = date;
        this.date_sent = date_sent;
        this.protocol = protocol;
        this.read = read;
        this.status = status;
        this.type = type;
        this.reply_path_present = reply_path_present;
        this.locked = locked;
        this.sim_id = sim_id;
        this.error_code = error_code;
        this.seen = seen;
        this.star = star;
        this.pri = pri;
        this.address = address;
        this.body = body;
        this.service_center = service_center;
    }

    public int getApp_sms_id() {
        return app_sms_id;
    }

    public int get_id() {
        return _id;
    }

    public int getThread_id() {
        return thread_id;
    }

    public int getM_size() {
        return m_size;
    }

    public int getPerson() {
        return person;
    }

    public int getDate() {
        return date;
    }

    public int getDate_sent() {
        return date_sent;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getRead() {
        return read;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public int getReply_path_present() {
        return reply_path_present;
    }

    public int getLocked() {
        return locked;
    }

    public int getSim_id() {
        return sim_id;
    }

    public int getError_code() {
        return error_code;
    }

    public int getSeen() {
        return seen;
    }

    public int getStar() {
        return star;
    }

    public int getPri() {
        return pri;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public String getService_center() {
        return service_center;
    }
}


package com.jok.archwizacja_sms;


import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

class SmsData {
    private Integer app_sms_id;
    private Integer _id;
    private Integer thread_id;
    private Integer m_size;
    private Integer person;
    private Integer date;
    private Integer date_sent;
    private Integer protocol;
    private Integer read;
    private Integer status;
    private Integer type;
    private Integer reply_path_present;
    private Integer locked;
    private Integer sim_id;
    private Integer error_code;
    private Integer seen;
    private Integer star;
    private Integer pri;
    private String address;
    private String body;
    private String service_center;

    SmsData(Integer app_sms_id, Integer _id, Integer thread_id, Integer m_size, Integer person, Integer date, Integer date_sent, Integer protocol, Integer read, Integer status, Integer type, Integer reply_path_present, Integer locked, Integer sim_id, Integer error_code, Integer seen, Integer star, Integer pri, String address, String body, String service_center) {
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

    public Integer getApp_sms_id() {
        return app_sms_id;
    }

    public Integer get_id() {
        return _id;
    }

    public Integer getThread_id() {
        return thread_id;
    }

    public Integer getM_size() {
        return m_size;
    }

    public Integer getPerson() {
        return person;
    }

    public Integer getDate() {
        return date;
    }

    public Integer getDate_sent() {
        return date_sent;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public Integer getRead() {
        return read;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getType() {
        return type;
    }

    public Integer getReply_path_present() {
        return reply_path_present;
    }

    public Integer getLocked() {
        return locked;
    }

    public Integer getSim_id() {
        return sim_id;
    }

    public Integer getError_code() {
        return error_code;
    }

    public Integer getSeen() {
        return seen;
    }

    public Integer getStar() {
        return star;
    }

    public Integer getPri() {
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


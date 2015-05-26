package com.jok.archwizacja_sms;


import android.provider.Telephony;

public final class SMS {
    public static final String ID = "_id";
    public static final String THREAD_ID = Telephony.TextBasedSmsColumns.THREAD_ID;
    public static final String ADDRESS = Telephony.TextBasedSmsColumns.ADDRESS;
    public static final String M_SIZE = "m_size";
    public static final String PERSON = Telephony.TextBasedSmsColumns.PERSON;
    public static final String DATE = Telephony.TextBasedSmsColumns.DATE;
    public static final String DATE_SENT = Telephony.TextBasedSmsColumns.DATE_SENT;
    public static final String PROTOCOL = Telephony.TextBasedSmsColumns.PROTOCOL;
    public static final String READ = Telephony.TextBasedSmsColumns.READ;
    public static final String STATUS = Telephony.TextBasedSmsColumns.STATUS;
    public static final String TYPE = Telephony.TextBasedSmsColumns.TYPE;
    public static final String REPLY_PATH_PRESENT = Telephony.TextBasedSmsColumns.REPLY_PATH_PRESENT;
    public static final String SUBJECT = Telephony.TextBasedSmsColumns.SUBJECT;
    public static final String BODY = Telephony.TextBasedSmsColumns.BODY;
    public static final String SERVICE_CENTER = Telephony.TextBasedSmsColumns.SERVICE_CENTER;
    public static final String LOCKED = Telephony.TextBasedSmsColumns.LOCKED;
    public static final String SIM_ID = "sim_id";
    public static final String ERROR_CODE = Telephony.TextBasedSmsColumns.ERROR_CODE;
    public static final String SEEN = Telephony.TextBasedSmsColumns.SEEN;
    public static final String STAR = "star";
    public static final String PRI = "pri";

    private SMS() {}

    public static class Data {
        public final boolean full;
        public final int _id;
        public final int thread_id;
        public final String address;
        public final int m_size;
        public final int person;
        public final int date;
        public final int date_sent;
        public final int protocol;
        public final int read;
        public final int status;
        public final int type;
        public final int reply_path_present;
        public final String subject;
        public final String body;
        public final String service_center;
        public final int locked;
        public final int sim_id;
        public final int error_code;
        public final int seen;
        public final int star;
        public final int pri;

        public Data(int id, int thread_id, String address, int date, int type, String body) {
            full = false;
            _id = id;
            this.thread_id = thread_id;
            this.address = address;
            this.date = date;
            this.type = type;
            this.body = body;
            m_size = 0;
            person = 0;
            date_sent = 0;
            protocol = 0;
            read = 0;
            status = 0;
            reply_path_present = 0;
            subject = null;
            service_center = null;
            locked = 0;
            sim_id = 0;
            error_code = 0;
            seen = 0;
            star = 0;
            pri = 0;
        }

        public Data(int id, int thread_id, String address, int m_size, int person, int date, int date_sent, int protocol, int read, int status, int type, int reply_path_present, String subject, String body, String service_center, int locked, int sim_id, int error_code, int seen, int star, int pri) {
            full = true;
            _id = id;
            this.thread_id = thread_id;
            this.address = address;
            this.m_size = m_size;
            this.person = person;
            this.date = date;
            this.date_sent = date_sent;
            this.protocol = protocol;
            this.read = read;
            this.status = status;
            this.type = type;
            this.reply_path_present = reply_path_present;
            this.subject = subject;
            this.body = body;
            this.service_center = service_center;
            this.locked = locked;
            this.sim_id = sim_id;
            this.error_code = error_code;
            this.seen = seen;
            this.star = star;
            this.pri = pri;
        }
    }
}

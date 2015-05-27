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
        public final String _id;
        public final String thread_id;
        public final String address;
        public final String m_size;
        public final String person;
        public final String date;
        public final String date_sent;
        public final String protocol;
        public final String read;
        public final String status;
        public final String type;
        public final String reply_path_present;
        public final String subject;
        public final String body;
        public final String service_center;
        public final String locked;
        public final String sim_id;
        public final String error_code;
        public final String seen;
        public final String star;
        public final String pri;

        public Data(String id, String thread_id, String address, String m_size, String person, String date, String date_sent, String protocol, String read, String status, String type, String reply_path_present, String subject, String body, String service_center, String locked, String sim_id, String error_code, String seen, String star, String pri) {
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

package com.jok.archwizacja_sms;

import java.io.Serializable;

/**
 * Opakowanie do przesyłania obiektów przez intenty.
 */
public class DataWrapper implements Serializable {
    private int[] ids;
    private String[] names;

    public DataWrapper(int[] ids, String[] names) {
        this.ids = ids;
        this.names = names;
    }

    public int[] getIds() {
        return ids;
    }

    public String[] getNames() {
        return names;
    }
}


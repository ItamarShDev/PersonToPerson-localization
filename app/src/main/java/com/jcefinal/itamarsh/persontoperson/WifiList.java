package com.jcefinal.itamarsh.persontoperson;

import android.provider.BaseColumns;

/**
 * Created by itamar on 11-Jun-16.
 */
public class WifiList {
    public WifiList() {
    }

    public static abstract class WifiTable implements BaseColumns {
        public static final String TABLE_NAME = "WIFI";
        public static final String BSSID = "BSSID";
        public static final String SIGNAL = "signal";
        public static final String FREQUENCY = "frequency";
        public static final String CHANNEL = "channel";
    }
}

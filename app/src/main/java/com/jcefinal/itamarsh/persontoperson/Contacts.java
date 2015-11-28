package com.jcefinal.itamarsh.persontoperson;

import android.media.Image;
import android.provider.BaseColumns;

/**
 * Created by itamar on 27-Nov-15.
 */
public class Contacts {
    public Contacts(){}
    public static abstract class ContactsTable implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String userName = "name";
        public static final String userID = "id";
        public static final String btId = "BT_Id";
        public static final String phoneNum = "phone";
        public static final String wifiId = "WiFi_Id";
    }
}

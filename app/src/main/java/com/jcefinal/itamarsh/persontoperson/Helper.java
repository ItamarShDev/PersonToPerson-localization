package com.jcefinal.itamarsh.persontoperson;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by olesya on 23-Dec-15
 * This class have global functions that can be used in all other classes
 */
public class Helper {
    public static final String REQUEST = "I Want to Search For You";
    public static final String REFUSE = "Sorry friend. I'm hiding from you ;)";
    public static final String APPROVED = "Commencing search ...";
    public static final String EXIST = "contact exist";
    public static final String NOT_EXIST = "contact not exist";
    /* This function responsible for encoding string to SHA-256*/
    public String encode(String num)
    {
        String hashString = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(num.getBytes());
            byte[] digest = md.digest();
            hashString =  Base64.encodeToString(digest, Base64.DEFAULT);
        }
        catch (NoSuchAlgorithmException e)
        {}
        return hashString;
    }
}

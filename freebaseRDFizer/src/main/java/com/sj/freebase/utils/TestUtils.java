package com.sj.freebase.utils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.codec.binary.Base64;

public class TestUtils {

    private static final String base64Digits =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";


    public static String base64EncodeId(Long id, int base) {
        StringBuffer baseConvertedString = new StringBuffer();
        while (id != 0) {
            // System.out.println("t -- " + (input % 64));
            baseConvertedString.append(base64Digits.charAt((int) (id % base)));
            id = id / base;
        }

        return baseConvertedString.reverse().toString();
    }


    public static Long base64DecodeId(StringBuffer stringId, int base) {
        Long decodedId = 0L;

        String id = stringId.reverse().toString();

        for (int i = 0; i < id.length(); i++) {
            decodedId +=
                (base64Digits.indexOf(id.charAt(i)) * (long) Math.pow(base, i));
        }

        return decodedId;
    }


    public static void main(String [] args) {
        System.out.println("Start");
        for (long i = 0; i <= Long.MAX_VALUE; i++) {

            String shortenedId = base64EncodeId(i, 64);

            System.out.println("Shortened id of " + i + " is " + shortenedId);
            long decodedId = base64DecodeId(new StringBuffer(shortenedId), 64);

            if (decodedId != i) {
                System.out.println("Encoding for i = " + i + "failed");
                break;
            }
        }
        // System.out.println(Long.MAX_VALUE % 64);
        // System.out.println(Long.MAX_VALUE / 64);
        // System.out.println(Long.MAX_VALUE);
    }
}

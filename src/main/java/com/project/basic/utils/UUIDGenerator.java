package com.project.basic.utils;

import java.util.UUID;

public abstract class UUIDGenerator {
    public static String generateTransactionId() {
        return "tid_" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String randomString(int len) {
        String asctbl = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_";
        String str = "";
        for (int i = 0; i < len; ++i) {
            int offset = (int) Math.round(Math.random() * (asctbl.length() - 1));
            str = str + asctbl.substring(offset, offset + 1);
        }
        return str;
    }
}
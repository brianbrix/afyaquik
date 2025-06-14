package com.afyaquik.utils;

import java.security.SecureRandom;

public class BatchNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public static String generateBatchNumber() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    public static String generateBatchNumberWithDate() {
        String datePart = new java.text.SimpleDateFormat("yyMMdd").format(new java.util.Date());
        String randomPart = generateBatchNumber();
        return datePart + "-" + randomPart;
    }
}

package com.eaglebank.feature.account.service;

import java.util.Random;

public class BankAccountUtils {

    public static String generateAccountNumber() {
        Random random = new Random();

        // Generate a number between 10000000 and 99999999 (inclusive)
        int accountNumber = 10000000 + random.nextInt(90000000);

        return String.valueOf(accountNumber);
    }

    public static String generateSortCode() {
        Random random = new Random();

        // Each segment of the sort code must be two digits, from 00 to 99
        int part1 = random.nextInt(100); // 00–99
        int part2 = random.nextInt(100);
        int part3 = random.nextInt(100);

        // Format each part with leading zeros if needed
        return String.format("%02d-%02d-%02d", part1, part2, part3);
    }
}

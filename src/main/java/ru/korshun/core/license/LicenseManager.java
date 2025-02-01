package ru.korshun.core.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LicenseManager {
    private static List<License> licenses = new ArrayList<>();

    public static List<License> getLicenses() {
        return licenses;
    }

    public static void add(License license) {
        licenses.add(license);
    }

    public static void remove(License license) {
        licenses.remove(license);
    }

    public static boolean contains(License license) {
        return licenses.contains(license);
    }

    public static License random() {
        Random random = new Random();
        return licenses.get(random.nextInt(licenses.size()));
    }
}

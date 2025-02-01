package ru.korshun.core.license;

public class License {
    private String key;

    public License(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean isValid() {
        return LicenseManager.contains(this);
    }
}

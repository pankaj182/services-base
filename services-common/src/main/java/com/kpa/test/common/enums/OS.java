package com.kpa.test.common.enums;

import java.util.Locale;

public enum OS {
    WINDOWS,
    LINUX,
    MAC,
    UNKNOWN;

    public static OS getOS(String os) {
        os = os.toLowerCase();
        switch (os) {
            case "windows":
                return WINDOWS;
            case "linux":
            case "unix":
            case "fedora":
            case "redhat":
            case "centos":
            case "ubuntu":
            case "debian":
                return LINUX;
            case "mac":
            case "darwin":
                return MAC;
            default:
                return UNKNOWN;
        }
    }
}

package util;

import java.util.UUID;

public class UUIDUtil {
    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static String generateShort() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

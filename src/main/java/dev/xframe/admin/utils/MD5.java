package dev.xframe.admin.utils;

import dev.xframe.utils.XCaught;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String encrypt(String txt) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(txt.getBytes(StandardCharsets.UTF_8));
            byte[] byteArray = md5.digest();
            StringBuilder res = new StringBuilder();
            for (byte b : byteArray) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    res.append("0");
                }
                res.append(hex);
            }
            return res.toString();
        } catch (NoSuchAlgorithmException e) {
            throw XCaught.throwException(e);
        }
    }
}

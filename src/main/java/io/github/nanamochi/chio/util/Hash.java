package io.github.nanamochi.chio.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
  public static String md5Hex(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(input.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}

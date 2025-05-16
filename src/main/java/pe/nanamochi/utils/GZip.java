package pe.nanamochi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

public class GZip {

  public static byte[] compress(byte[] data, boolean includeMTime) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    GzipParameters params = new GzipParameters();
    params.setCompressionLevel(9);
    if (includeMTime) {
      long mtime = System.currentTimeMillis() / 1000;
      params.setModificationTime(mtime * 1000L);
    }

    try (GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(bos, params)) {
      gzip.write(data);
    }

    return bos.toByteArray();
  }

  public static byte[] decompress(byte[] data) throws IOException {
    if (data.length == 0) {
      return new byte[0];
    }

    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bis);
        ByteArrayOutputStream dst = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int len;
      while ((len = gzipIn.read(buffer)) != -1) {
        dst.write(buffer, 0, len);
      }

      return dst.toByteArray();
    }
  }
}

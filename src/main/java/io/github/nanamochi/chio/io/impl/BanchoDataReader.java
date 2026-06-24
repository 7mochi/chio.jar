package io.github.nanamochi.chio.io.impl;

import io.github.nanamochi.chio.io.DataReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class BanchoDataReader implements DataReader {
  @Override
  public long readUInt64(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 8);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
  }

  @Override
  public long readInt64(InputStream input) throws IOException {
    return readUInt64(input);
  }

  @Override
  public long readUInt32(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 4);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
  }

  @Override
  public int readInt32(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 4);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
  }

  @Override
  public int readUInt16(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 2);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
  }

  @Override
  public short readInt16(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 2);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
  }

  @Override
  public int readUInt8(InputStream input) throws IOException {
    return (byte) input.read();
  }

  @Override
  public byte readInt8(InputStream input) throws IOException {
    return (byte) input.read();
  }

  @Override
  public boolean readBoolean(InputStream input) throws IOException {
    return input.read() != 0;
  }

  @Override
  public float readFloat32(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 4);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
  }

  @Override
  public double readFloat64(InputStream input) throws IOException {
    byte[] bytes = readBytes(input, 8);
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
  }

  @Override
  public int[] readIntList16(InputStream input) throws IOException {
    int length = readUInt16(input);
    int[] result = new int[length];
    for (int i = 0; i < length; i++) {
      result[i] = readInt32(input);
    }
    return result;
  }

  @Override
  public int[] readIntList32(InputStream input) throws IOException {
    long length = readUInt32(input);
    int[] result = new int[(int) length];
    for (int i = 0; i < length; i++) {
      result[i] = readInt32(input);
    }
    return result;
  }

  @Override
  public List<Boolean> readBoolList(InputStream input, int count) throws IOException {
    List<Boolean> result = new ArrayList<>(count);
    int packed = readUInt8(input);
    for (int i = 0; i < count; i++) {
      result.add((packed & (1 << i)) != 0);
    }
    return result;
  }

  @Override
  public String readString(InputStream input) throws IOException {
    if (input.read() == 0) {
      return "";
    }
    int length = readULEB128(input);
    if (length == 0) {
      return "";
    }
    byte[] bytes = readBytes(input, length);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static int readULEB128(InputStream input) throws IOException {
    int result = 0;
    int shift = 0;
    while (true) {
      int b = input.read();
      if (b == -1) {
        throw new IOException("Unexpected end of stream in ULEB128");
      }
      result |= (b & 0x7F) << shift;
      if ((b & 0x80) == 0) {
        return result;
      }
      shift += 7;
    }
  }

  private byte[] readBytes(InputStream input, int length) throws IOException {
    byte[] buffer = new byte[length];
    int offset = 0;
    while (offset < length) {
      int read = input.read(buffer, offset, length - offset);
      if (read == -1) {
        throw new IOException("Unexpected end of stream");
      }
      offset += read;
    }
    return buffer;
  }
}

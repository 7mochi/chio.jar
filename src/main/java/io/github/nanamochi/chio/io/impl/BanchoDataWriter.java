package io.github.nanamochi.chio.io.impl;

import io.github.nanamochi.chio.io.DataWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class BanchoDataWriter implements DataWriter {
  @Override
  public void writeUInt64(OutputStream output, long value) throws IOException {
    output.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array());
  }

  @Override
  public void writeInt64(OutputStream output, long value) throws IOException {
    writeUInt64(output, value);
  }

  @Override
  public void writeUInt32(OutputStream output, long value) throws IOException {
    output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) value).array());
  }

  @Override
  public void writeInt32(OutputStream output, int value) throws IOException {
    output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array());
  }

  @Override
  public void writeUInt16(OutputStream output, int value) throws IOException {
    output.write(
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) value).array());
  }

  @Override
  public void writeInt16(OutputStream output, int value) throws IOException {
    writeUInt16(output, value);
  }

  @Override
  public void writeUInt8(OutputStream output, int value) throws IOException {
    output.write(value & 0xFF);
  }

  @Override
  public void writeInt8(OutputStream output, int value) throws IOException {
    output.write(value);
  }

  @Override
  public void writeBoolean(OutputStream output, boolean value) throws IOException {
    output.write(value ? 1 : 0);
  }

  @Override
  public void writeFloat32(OutputStream output, float value) throws IOException {
    output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array());
  }

  @Override
  public void writeFloat64(OutputStream output, double value) throws IOException {
    output.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(value).array());
  }

  @Override
  public void writeIntList16(OutputStream output, int[] values) throws IOException {
    writeUInt16(output, values.length);
    for (int value : values) {
      writeInt32(output, value);
    }
  }

  @Override
  public void writeIntList32(OutputStream output, int[] values) throws IOException {
    writeUInt32(output, values.length);
    for (int value : values) {
      writeInt32(output, value);
    }
  }

  @Override
  public void writeBoolList(OutputStream output, List<Boolean> values) throws IOException {
    int packed = 0;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i)) {
        packed |= (1 << i);
      }
    }
    output.write(packed);
  }

  @Override
  public void writeString(OutputStream output, String value) throws IOException {
    if (value == null || value.isEmpty()) {
      output.write(0);
      return;
    }

    output.write(0x0B);
    byte[] utf8Bytes = value.getBytes(StandardCharsets.UTF_8);

    writeULEB128(output, utf8Bytes.length);
    output.write(utf8Bytes);
  }

  public static void writeULEB128(OutputStream output, int value) throws IOException {
    do {
      int b = value & 0x7F;
      value >>>= 7;
      if (value != 0) {
        b |= 0x80;
      }
      output.write(b);
    } while (value != 0);
  }
}

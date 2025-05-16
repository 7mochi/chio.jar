package pe.nanamochi.io.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BanchoDataWriter implements IDataWriter {

  @Override
  public void writeUint64(OutputStream output, long value) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    buffer.putLong(value);
    output.write(buffer.array());
  }

  @Override
  public void writeInt64(OutputStream output, long value) throws IOException {
    writeUint64(output, value);
  }

  @Override
  public void writeUint32(OutputStream output, long value) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    buffer.putInt((int) value);
    output.write(buffer.array());
  }

  @Override
  public void writeInt32(OutputStream output, int value) throws IOException {
    writeUint32(output, value);
  }

  @Override
  public void writeUint16(OutputStream output, int value) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
    buffer.putShort((short) value);
    output.write(buffer.array());
  }

  @Override
  public void writeInt16(OutputStream output, short value) throws IOException {
    writeUint16(output, value);
  }

  @Override
  public void writeUint8(OutputStream output, int value) throws IOException {
    output.write(value & 0xFF);
  }

  @Override
  public void writeInt8(OutputStream output, byte value) throws IOException {
    writeUint8(output, value);
  }

  @Override
  public void writeBoolean(OutputStream output, boolean value) throws IOException {
    output.write(value ? 1 : 0);
  }

  @Override
  public void writeFloat32(OutputStream output, float value) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    buffer.putFloat(value);
    output.write(buffer.array());
  }

  @Override
  public void writeFloat64(OutputStream output, double value) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    buffer.putDouble(value);
    output.write(buffer.array());
  }

  @Override
  public void writeIntList16(OutputStream output, List<Integer> list) throws IOException {
    writeUint16(output, list.size());
    for (int value : list) {
      writeInt32(output, value);
    }
  }

  @Override
  public void writeIntList32(OutputStream output, List<Integer> list) throws IOException {
    writeUint32(output, list.size());
    for (int value : list) {
      writeInt32(output, value);
    }
  }

  @Override
  public void writeBoolList(OutputStream output, List<Boolean> bools) throws IOException {
    if (bools.size() < 8) {
      throw new IOException("bool list must have at least 8 elements");
    }

    byte result = 0;
    for (int i = 7; i >= 0; i--) {
      if (bools.get(i)) {
        result |= 1;
      }
      if (i > 0) {
        result <<= 1;
      }
    }
    writeUint8(output, result);
  }

  @Override
  public void writeString(OutputStream output, String value) throws IOException {
    if (value == null || value.isEmpty()) {
      writeUint8(output, 0x00);
      return;
    }

    writeUint8(output, 0x0b);
    byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);

    output.write(uleb128Encode(strBytes.length));
    output.write(strBytes);
  }

  private static byte[] uleb128Encode(int value) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    do {
      byte b = (byte) (value & 0x7F);
      value >>>= 7;
      if (value != 0) {
        b |= (byte) 0x80;
      }
      stream.write(b);
    } while (value != 0);
    return stream.toByteArray();
  }
}

package pe.nanamochi.io.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BanchoDataReader implements IDataReader {

  @Override
  public long readUint64(InputStream in) throws IOException {
    byte[] buffer = new byte[8];
    if (in.read(buffer) != 8) {
      throw new IOException("Failed to read 8 bytes");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer.getLong();
  }

  @Override
  public long readInt64(InputStream in) throws IOException {
    return readUint64(in);
  }

  @Override
  public int readUint32(InputStream in) throws IOException {
    byte[] buffer = new byte[4];
    if (in.read(buffer) != 4) {
      throw new IOException("Failed to read 4 bytes");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer.getInt();
  }

  @Override
  public int readInt32(InputStream in) throws IOException {
    return readUint32(in);
  }

  @Override
  public short readUint16(InputStream in) throws IOException {
    byte[] buffer = new byte[2];
    if (in.read(buffer) != 2) {
      throw new IOException("Failed to read 2 bytes");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer.getShort();
  }

  @Override
  public short readInt16(InputStream in) throws IOException {
    return readUint16(in);
  }

  @Override
  public byte readUint8(InputStream in) throws IOException {
    int value = in.read();
    if (value == -1) {
      throw new IOException("Failed to read 1 byte");
    }
    return (byte) value;
  }

  @Override
  public byte readInt8(InputStream in) throws IOException {
    return readUint8(in);
  }

  @Override
  public boolean readBoolean(InputStream in) throws IOException {
    byte value = readUint8(in);
    return value != 0;
  }

  @Override
  public float readFloat32(InputStream in) throws IOException {
    byte[] buffer = new byte[4];
    if (in.read(buffer) != 4) {
      throw new IOException("Failed to read 4 bytes");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer.getFloat();
  }

  @Override
  public double readFloat64(InputStream in) throws IOException {
    byte[] buffer = new byte[8];
    if (in.read(buffer) != 8) {
      throw new IOException("Failed to read 8 bytes");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer.getDouble();
  }

  @Override
  public int[] readIntList16(InputStream in) throws IOException {
    int length = readUint16(in);
    int[] list = new int[length];
    for (int i = 0; i < length; i++) {
      list[i] = readInt32(in);
    }
    return list;
  }

  @Override
  public int[] readIntList32(InputStream in) throws IOException {
    int length = readUint32(in);
    int[] list = new int[length];
    for (int i = 0; i < length; i++) {
      list[i] = readInt32(in);
    }
    return list;
  }

  @Override
  public boolean[] readBoolList(InputStream in) throws IOException {
    byte input = readUint8(in);
    boolean[] bools = new boolean[8];
    for (int i = 0; i < 8; i++) {
      bools[i] = ((input >> i) & 1) > 0;
    }
    return bools;
  }

  @Override
  public String readString(InputStream in) throws IOException {
    byte b = readUint8(in);
    if (b == 0x00) {
      return "";
    }

    if (b != 0x0b) {
      throw new IOException("Invalid string type");
    }

    int length = uleb128Decode(in);
    byte[] buf = new byte[length];
    if (in.read(buf) != length) {
      throw new IOException("Failed to read string data");
    }
    return new String(buf, "UTF-8");
  }

  private int uleb128Decode(InputStream in) throws IOException {
    int value = 0;
    int shift = 0;
    int byteRead;
    do {
      byteRead = in.read();
      if (byteRead == -1) {
        throw new IOException("Unexpected end of stream");
      }
      value |= (byteRead & 0x7F) << shift;
      shift += 7;
    } while ((byteRead & 0x80) != 0);
    return value;
  }
}

package pe.nanamochi.io.data;

import java.io.IOException;
import java.io.InputStream;

public interface IDataReader {

  long readUint64(InputStream in) throws IOException;

  long readInt64(InputStream in) throws IOException;

  int readUint32(InputStream in) throws IOException;

  int readInt32(InputStream in) throws IOException;

  short readUint16(InputStream in) throws IOException;

  short readInt16(InputStream in) throws IOException;

  byte readUint8(InputStream in) throws IOException;

  byte readInt8(InputStream in) throws IOException;

  boolean readBoolean(InputStream in) throws IOException;

  float readFloat32(InputStream in) throws IOException;

  double readFloat64(InputStream in) throws IOException;

  int[] readIntList16(InputStream in) throws IOException;

  int[] readIntList32(InputStream in) throws IOException;

  boolean[] readBoolList(InputStream in) throws IOException;

  String readString(InputStream in) throws IOException;
}

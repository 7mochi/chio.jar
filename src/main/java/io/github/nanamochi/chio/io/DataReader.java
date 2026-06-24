package io.github.nanamochi.chio.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DataReader {
  long readUInt64(InputStream input) throws IOException;

  long readInt64(InputStream input) throws IOException;

  long readUInt32(InputStream input) throws IOException;

  int readInt32(InputStream input) throws IOException;

  int readUInt16(InputStream input) throws IOException;

  short readInt16(InputStream input) throws IOException;

  int readUInt8(InputStream input) throws IOException;

  byte readInt8(InputStream input) throws IOException;

  boolean readBoolean(InputStream input) throws IOException;

  float readFloat32(InputStream input) throws IOException;

  double readFloat64(InputStream input) throws IOException;

  int[] readIntList16(InputStream input) throws IOException;

  int[] readIntList32(InputStream input) throws IOException;

  List<Boolean> readBoolList(InputStream input, int count) throws IOException;

  String readString(InputStream input) throws IOException;
}

package io.github.nanamochi.chio.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface DataWriter {
  void writeUInt64(OutputStream output, long value) throws IOException;

  void writeInt64(OutputStream output, long value) throws IOException;

  void writeUInt32(OutputStream output, long value) throws IOException;

  void writeInt32(OutputStream output, int value) throws IOException;

  void writeUInt16(OutputStream output, int value) throws IOException;

  void writeInt16(OutputStream output, int value) throws IOException;

  void writeUInt8(OutputStream output, int value) throws IOException;

  void writeInt8(OutputStream output, int value) throws IOException;

  void writeBoolean(OutputStream output, boolean value) throws IOException;

  void writeFloat32(OutputStream output, float value) throws IOException;

  void writeFloat64(OutputStream output, double value) throws IOException;

  void writeIntList16(OutputStream output, int[] values) throws IOException;

  void writeIntList32(OutputStream output, int[] values) throws IOException;

  void writeBoolList(OutputStream output, List<Boolean> values) throws IOException;

  void writeString(OutputStream output, String value) throws IOException;
}

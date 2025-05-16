package pe.nanamochi.io.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface IDataWriter {

  void writeUint64(OutputStream out, long v) throws IOException;

  void writeInt64(OutputStream out, long v) throws IOException;

  void writeUint32(OutputStream out, long v) throws IOException;

  void writeInt32(OutputStream out, int v) throws IOException;

  void writeUint16(OutputStream out, int v) throws IOException;

  void writeInt16(OutputStream out, short v) throws IOException;

  void writeUint8(OutputStream out, int v) throws IOException;

  void writeInt8(OutputStream out, byte v) throws IOException;

  void writeBoolean(OutputStream out, boolean v) throws IOException;

  void writeFloat32(OutputStream out, float v) throws IOException;

  void writeFloat64(OutputStream out, double v) throws IOException;

  void writeIntList16(OutputStream out, List<Integer> list) throws IOException;

  void writeIntList32(OutputStream out, List<Integer> list) throws IOException;

  void writeBoolList(OutputStream out, List<Boolean> bools) throws IOException;

  void writeString(OutputStream out, String value) throws IOException;
}

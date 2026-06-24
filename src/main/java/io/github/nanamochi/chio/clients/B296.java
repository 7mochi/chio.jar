package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.ScoreFrame;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// b296 adds the "Time" value to score frames.
public class B296 extends B294 {

  // region Reader Methods

  @Override
  protected ScoreFrame readScoreFrame(InputStream input) throws IOException {
    // TODO: Validate checksum
    reader.readString(input);

    int time = reader.readInt32(input);
    int id = reader.readUInt8(input);
    int total300 = reader.readUInt16(input);
    int total100 = reader.readUInt16(input);
    int total50 = reader.readUInt16(input);
    int totalGeki = reader.readUInt16(input);
    int totalKatu = reader.readUInt16(input);
    int totalMiss = reader.readUInt16(input);
    long totalScore = reader.readUInt32(input);
    int maxCombo = reader.readUInt16(input);
    int currentCombo = reader.readUInt16(input);
    boolean perfect = reader.readBoolean(input);
    int hp = reader.readUInt8(input);

    return new ScoreFrame(
        time,
        id,
        total300,
        total100,
        total50,
        totalGeki,
        totalKatu,
        totalMiss,
        (int) totalScore,
        maxCombo,
        currentCombo,
        perfect,
        hp,
        0);
  }

  // endregion
  // region Writer Methods

  @Override
  protected void writeScoreFrame(ByteArrayOutputStream buf, ScoreFrame frame) throws IOException {
    writer.writeString(buf, frame.getChecksum());
    writer.writeInt32(buf, frame.getTime());
    writer.writeUInt8(buf, frame.getId());
    writer.writeUInt16(buf, frame.getTotal300());
    writer.writeUInt16(buf, frame.getTotal100());
    writer.writeUInt16(buf, frame.getTotal50());
    writer.writeUInt16(buf, frame.getTotalGeki());
    writer.writeUInt16(buf, frame.getTotalKatu());
    writer.writeUInt16(buf, frame.getTotalMiss());
    writer.writeUInt32(buf, frame.getTotalScore());
    writer.writeUInt16(buf, frame.getMaxCombo());
    writer.writeUInt16(buf, frame.getCurrentCombo());
    writer.writeBoolean(buf, frame.isPerfect());
    writer.writeUInt8(buf, frame.getHp());
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 296;
  }

  // endregion
}

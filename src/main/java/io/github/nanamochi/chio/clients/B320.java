package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Message;
import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// b320 adds partial support for multiple channels
public class B320 extends B312 {

  // region Reader Methods

  @Override
  protected Message readMessage(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    return new Message(
        reader.readString(input), reader.readString(input), reader.readString(input), 0);
  }

  @Override
  protected Message readPrivateMessage(byte[] data) throws IOException {
    return readMessage(data);
  }

  // endregion
  // region Writer Methods

  @Override
  protected byte[] writeMessage(Message message) throws IOException {
    String content = message.getContent();

    // Automatically format chat links, if enabled
    if (isFormatChatLinks()) {
      content = message.getContentMarkdownFormatted();
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeString(buf, message.getSender());
    writer.writeString(buf, content);
    writer.writeString(buf, message.getTarget());
    return writePacket(PacketType.BANCHO_MESSAGE, buf.toByteArray());
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 320;
  }

  // endregion
}

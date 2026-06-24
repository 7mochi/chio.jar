package io.github.nanamochi.chio;

import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.IOException;
import java.util.List;

public interface BanchoIO {
  int getVersion();

  int getSlotSize();

  int getHeaderSize();

  int getProtocolVersion();

  boolean isFormatChatLinks();

  boolean isDisableCompression();

  boolean isRequiresStatusUpdates();

  List<String> getAutojoinChannels();

  Object readPacket(byte[] data) throws IOException;

  byte[] writePacket(PacketType type, Object... args) throws IOException;

  byte[] writePacket(PacketType type, byte[] data) throws IOException;

  byte[] writePacket(PacketType type, byte[] data, boolean includeMTime) throws IOException;

  String formatChatLink(String text, String url);
}

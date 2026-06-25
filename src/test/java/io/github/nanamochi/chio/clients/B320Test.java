package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.Message;
import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("B320 Client Tests")
public class B320Test {

  static B320 client;

  @BeforeAll
  static void setupClient() {
    client = (B320) ClientSelector.selectClient(320);
  }

  @Test
  @DisplayName("Message (#osu channel)")
  public void testMessagePacketToOsuChannel() throws IOException {
    assertArrayEquals(
        new byte[] {
          7, 0, 51, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          -29, -26, 45, 73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, -27,
          -26, 1, 115, 115, 83, -117, -117, 19, -45, 83, -71, 89, -108, -13, -117, 75,
          1, 27, -30, -75, -64, 35, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MESSAGE, new Message("test_username", "test_message", "#osu", 0)));
  }

  @Test
  @DisplayName("Message with markdown link")
  public void testMessageWithMarkdownLink() throws IOException {
    assertArrayEquals(
        new byte[] {
          7, 0, 81, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          -29, -26, 45, 73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, -27,
          -42, -14, 72, -51, -55, -55, 87, -48, 112, -83, 72, -52, 45, -56, 73, -43,
          -116, -50, 40, 41, 41, 40, -74, -46, -41, 79, -123, 8, -24, 37, -25, -25,
          -58, 42, -124, -25, 23, -27, -92, 112, -77, 40, -25, 23, -105, 2, 0, -16,
          72, 67, -76, 65, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MESSAGE,
            new Message("test_username", "Hello [https://example.com Example] World", "#osu", 0)));
  }
}

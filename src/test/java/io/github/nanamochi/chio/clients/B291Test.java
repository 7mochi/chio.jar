package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("B291 Client Tests")
public class B291Test {

  static B291 client;

  @BeforeAll
  static void setupClient() {
    client = (B291) ClientSelector.selectClient(291);
  }

  @Test
  @DisplayName("GetAttention")
  public void testBanchoGetAttentionPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          24, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_GET_ATTENTION));
  }

  @Test
  @DisplayName("Annoucement")
  public void testBanchoAnnouncementPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          25, 0, 26, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, 102, 41, 73, 45, 46, 1, 0,
          -80, 86, -66, -41, 6, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_ANNOUNCE, "test"));
  }

  @Test
  @DisplayName("Restart")
  public void testBanchoRestartPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          25, 0, 58, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, 86, 113, 74, -52, 75, -50,
          -56, 87, -56, 44, 86, 40, 74, 45, 46, 73, 44, 42, -55, -52, 75, -41, 81, 40, -56, 73, 77,
          44, 78, 85, 40, 79, -52, 44, -47, -45, -45, 3, 0, 116, 25, 113, -98, 38, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_RESTART, 1000));
  }
}

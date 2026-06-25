package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.UserInfo;
import io.github.nanamochi.chio.model.UserPresence;
import io.github.nanamochi.chio.model.UserStats;
import io.github.nanamochi.chio.model.UserStatus;
import io.github.nanamochi.chio.model.enums.Mode;
import io.github.nanamochi.chio.model.enums.Mods;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.Permissions;
import io.github.nanamochi.chio.model.enums.Status;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("B338 Client Tests")
public class B338Test {

  static B338 client;

  @BeforeAll
  static void setupClient() {
    client = (B338) ClientSelector.selectClient(338);
  }

  @Test
  @DisplayName("UserStats (normal user)")
  public void testUserStatsPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          12, 0, 0, 42, 0, 0, 0, 3, 0, 0, 0, 1, 0, 1, 11, 4,
          73, 100, 108, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, -56, 66, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
          0
        },
        client.writePacket(
            PacketType.BANCHO_USER_STATS,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }

  @Test
  @DisplayName("UserPresence (normal user)")
  public void testUserPresencePacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          12, 0, 0, 91, 0, 0, 0, 3, 0, 0, 0, 2, 0, 1, 11, 4,
          73, 100, 108, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, -56, 66, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
          0, 11, 13, 116, 101, 115, 116, 95, 117, 115, 101, 114, 110, 97, 109, 101,
          11, 9, 51, 95, 48, 48, 48, 46, 112, 110, 103, 19, 11, 20, 78, 101,
          116, 104, 101, 114, 108, 97, 110, 100, 115, 32, 65, 110, 116, 105, 108, 108,
          101, 115
        },
        client.writePacket(
            PacketType.BANCHO_USER_PRESENCE,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }

  @Test
  @DisplayName("UserStats (IRC user)")
  public void testUserStatsIRCUser() throws IOException {
    assertArrayEquals(
        new byte[] {
          11, 0, 0, 15, 0, 0, 0, 11, 13, 116, 101, 115, 116, 95, 117, 115, 101, 114, 110, 97, 109,
          101
        },
        client.writePacket(
            PacketType.BANCHO_USER_STATS,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }
}

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

@DisplayName("B323 Client Tests")
public class B323Test {

  static B323 client;

  @BeforeAll
  static void setupClient() {
    client = (B323) ClientSelector.selectClient(323);
  }

  @Test
  @DisplayName("UserStats (update_stats=true)")
  public void testUserStatsWithUpdate() throws IOException {
    assertArrayEquals(
        new byte[] {
          12, 0, 92, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          99, 102, 96, 96, 96, -28, -26, 45, 73, 45, 46, -119, 47, 45, 78, 45,
          -54, 75, -52, 77, 101, -128, -125, 19, 78, -116, 8, 14, 3, -120, -51, -51,
          105, 28, 111, 96, 96, -96, 87, -112, -105, 46, -52, 45, -30, -105, 90, -110,
          -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109, -109, 90,
          -52, -59, -51, -30, -103, -110, 3, 50, 4, 0, 45, -5, 75, 21, 92, 0,
          0, 0
        },
        client.writePacket(
            PacketType.BANCHO_USER_STATS,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, true),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }

  @Test
  @DisplayName("UserStats (update_stats=false)")
  public void testUserStatsWithoutUpdate() throws IOException {
    assertArrayEquals(
        new byte[] {
          12, 0, 32, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          99, 102, 0, 1, 110, 22, -49, -108, -100, 84, 6, 6, 6, 0, 34, 25,
          125, -46, 15, 0, 0, 0
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
  @DisplayName("UserStats (IRC user)")
  public void testUserStatsIRCUser() throws IOException {
    assertArrayEquals(
        new byte[] {
          11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          -29, -26, 45, 73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 5,
          0, -31, -98, -108, -36, 15, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_USER_STATS,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, true),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }

  @Test
  @DisplayName("UserPresence (2 concatenated packets)")
  public void testUserPresencePacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          12, 0, 92, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
          99, 102, 96, 96, 96, -28, -26, 45, 73, 45, 46, -119, 47, 45, 78, 45,
          -54, 75, -52, 77, 101, -128, -125, 19, 78, -116, 8, 14, 3, -120, -51, -51,
          105, 28, 111, 96, 96, -96, 87, -112, -105, 46, -52, 45, -30, -105, 90, -110,
          -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109, -109, 90,
          -52, -59, -51, -30, -103, -110, 3, 50, 4, 0, 45, -5, 75, 21, 92, 0,
          0, 0, 12, 0, 32, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0,
          2, -1, 99, 102, 0, 1, 110, 22, -49, -108, -100, 84, 6, 6, 6, 0,
          34, 25, 125, -46, 15, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_USER_PRESENCE,
            new UserInfo(
                3,
                "test_username",
                new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, true),
                new UserStats(1, 0, 0, 100.0f, 1, 100))));
  }
}

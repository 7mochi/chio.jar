package pe.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import pe.nanamochi.clients.B282;
import pe.nanamochi.objects.*;
import pe.nanamochi.objects.enums.*;
import pe.nanamochi.utils.ClientManager;

@DisplayName("B282 Client Tests")
public class B282Test {

  static B282 client;

  @BeforeAll
  static void setupClient() {
    client = (B282) ClientManager.getClient(282);
  }

  @Nested
  class WritePackets {

    ByteArrayOutputStream stream;

    @BeforeEach
    void setupOutputStream() {
      stream = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("LoginReply")
    public void testBanchoLoginReplyPacket() throws IOException {
      client.writeLoginReply(stream, 3);

      assertArrayEquals(
          new byte[] {
            5, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("Ping")
    public void testPingPacket() throws IOException {
      client.writePing(stream);

      assertArrayEquals(
          new byte[] {
            8, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("Message (#osu channel)")
    public void testMessagePacketToOsuChannel() throws IOException {
      client.writeMessage(stream, new Message("test_username", "test_message", "#osu", 3));

      assertArrayEquals(
          new byte[] {
            7, 0, 45, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, -27, -26, 1, 115, 115, 83, -117, -117, 19, -45, 83, 1,
            -42, 20, -90, 3, 29, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("Message (Unsupported channel)")
    public void testMessagePacketToTestChannel() throws IOException {
      client.writeMessage(stream, new Message("test_username", "test_message", "#test", 3));

      assertArrayEquals(new byte[] {}, stream.toByteArray());
    }

    @Test
    @DisplayName("IrcChangeUsername")
    public void testIrcChangeUsernamePacket() throws IOException {
      client.writeIrcChangeUsername(stream, "old_username", "new_username");

      assertArrayEquals(
          new byte[] {
            9, 0, 41, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -106, -55, -49, 73, -119, 47,
            45, 78, 45, -54, 75, -52, 77, -75, 3, -126, -68, -44, 114, 56, 31, 0, 78, -66, -48, -58,
            30, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UsersStats (Normal user, Action: Idle)")
    public void testUserStatsPacketNormalUserIdle() throws IOException {
      client.writeUserStats(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37,
            7, -92, 67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81,
            36, 51, 39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UsersStats (Normal user, Action: Lobby)")
    public void testUserStatsPacketNormalUserLobby() throws IOException {
      client.writeUserStats(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30,
            -105, 90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103,
            -109, -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UsersStats (IRC user, Action: Idle)")
    public void testUserStatsPacketIRCUserIdle() throws IOException {
      client.writeUserStats(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserQuit (Normal user)")
    public void testUserQuitPacketNormalUser() throws IOException {
      client.writeUserQuit(
          stream,
          new UserQuit(
              new UserInfo(
                  3,
                  "test_username",
                  new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                  new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                  new UserStats(1, 0, 0, 100.0, 1, 100)),
              QuitState.GONE));

      assertArrayEquals(
          new byte[] {
            13, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30,
            -105, 90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103,
            -109, -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserQuit (IRC user)")
    public void testUserQuitPacketIRCUser() throws IOException {
      client.writeUserQuit(
          stream,
          new UserQuit(
              new UserInfo(
                  3,
                  "test_username",
                  new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                  new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                  new UserStats(1, 0, 0, 100.0, 1, 100)),
              QuitState.IRC_REMAINING));

      assertArrayEquals(
          new byte[] {
            13, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("SpectatorJoined")
    public void testSpectatorJoinedPacket() throws IOException {
      client.writeSpectatorJoined(stream, 3);

      assertArrayEquals(
          new byte[] {
            14, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("SpectatorLeft")
    public void testSpectatorLeftPacket() throws IOException {
      client.writeSpectatorLeft(stream, 3);

      assertArrayEquals(
          new byte[] {
            15, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("SpectateFrames")
    public void testSpectateFramesPacket() throws IOException {
      client.writeSpectateFrames(
          stream,
          new ReplayFrameBundle(
              ReplayAction.STANDARD,
              List.of(
                  new ReplayFrame(ButtonState.RIGHT_1.getValue(), 0.0, 0.0, 0),
                  new ReplayFrame(ButtonState.LEFT_2.getValue(), 0.0, 0.0, 1),
                  new ReplayFrame(ButtonState.SMOKE.getValue(), 0.0, 0.0, 2)),
              new ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5, 0),
              0));

      assertArrayEquals(
          new byte[] {
            16, 0, 33, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 100, 64, 2,
            -116, 88, 88, 32, -64, 4, 34, 0, -61, 103, 87, -106, 45, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("VersionUpdate")
    public void testVersionUpdatePacket() throws IOException {
      client.writeVersionUpdate(stream);

      assertArrayEquals(
          new byte[] {
            20, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("SpectatorCantSpectate")
    public void testSpectatorCantSpectatePacket() throws IOException {
      client.writeSpectatorCantSpectate(stream, 3);

      assertArrayEquals(
          new byte[] {
            23, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresence (Normal user, Action: Idle)")
    public void testUserPrsencePacketNormalUserIdle() throws IOException {
      client.writeUserPresence(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37,
            7, -92, 67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81,
            36, 51, 39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresence (Normal user, Action: Lobby)")
    public void testUserPresencePacketNormalUserLobby() throws IOException {
      client.writeUserPresence(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30,
            -105, 90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103,
            -109, -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresence (IRC user, Action: Idle)")
    public void testUserPresencePacketIRCUserIdle() throws IOException {
      client.writeUserPresence(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresenceSingle (Normal user, Action: Idle)")
    public void testUserPrsenceSinglePacketNormalUserIdle() throws IOException {
      client.writeUserPresenceSingle(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37,
            7, -92, 67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81,
            36, 51, 39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresenceSingle (Normal user, Action: Lobby)")
    public void testUserPresenceSinglePacketNormalUserLobby() throws IOException {
      client.writeUserPresenceSingle(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30,
            -105, 90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103,
            -109, -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresenceSingle (IRC user, Action: Idle)")
    public void testUserPresenceSinglePacketIRCUserIdle() throws IOException {
      client.writeUserPresenceSingle(
          stream,
          new UserInfo(
              3,
              "test_username",
              new UserPresence(true, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
              new UserStatus(Status.IDLE, "Idle", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
              new UserStats(1, 0, 0, 100.0, 1, 100)));

      assertArrayEquals(
          new byte[] {
            11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("UserPresenceBundle (Normal users)")
    public void testUserPresenceBundlePacketNormalUsers() throws IOException {
      client.writeUserPresenceBundle(
          stream,
          List.of(
              new UserInfo(
                  3,
                  "test_username",
                  new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                  new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                  new UserStats(1, 0, 0, 100.0, 1, 100)),
              new UserInfo(
                  4,
                  "test_username_2",
                  new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                  new UserStatus(Status.LOBBY, "", List.of(Mods.NO_MOD), Mode.OSU, "", 0, false),
                  new UserStats(2, 0, 0, 99.0, 3, 50))));

      assertArrayEquals(
          new byte[] {
            12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45,
            73, 45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60,
            16, -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30,
            -105, 90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103,
            -109, -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0, 12, 0, 88, 0, 0, 0, 31, -117,
            8, 0, 0, 0, 0, 0, 2, -1, 99, 97, 96, 96, -32, -26, 47, 73, 45, 46, -119, 47, 45, 78, 45,
            -54, 75, -52, 77, -115, 55, 98, 64, 6, 7, 34, 28, -104, -111, -72, 76, 32, 13, -100, 38,
            -15, 6, 6, 6, 122, 5, 121, -23, 92, 32, 49, 97, 110, 17, -65, -44, -110, -116, -44, -94,
            -100, -60, -68, -108, 98, 5, -57, -68, -110, -52, -100, -100, -44, 98, 0, -86, -86, 15,
            20, 92, 0, 0, 0
          },
          stream.toByteArray());
    }
  }

  @Nested
  class ReadPackets {

    ByteArrayInputStream stream;

    @Test
    @DisplayName("UsersStatus")
    public void testUserStatsPacket() throws IOException {
      stream =
          new ByteArrayInputStream(
              new byte[] {
                2, 11, 46, 77, 117, 116, 115, 117, 104, 105, 107, 111, 32, 73, 122, 117, 109, 105,
                32, 45, 32, 82, 101, 100, 32, 71, 111, 111, 115, 101, 32, 91, 82, 97, 105, 110, 98,
                111, 119, 32, 66, 101, 103, 105, 110, 110, 101, 114, 93, 11, 32, 49, 56, 97, 98, 48,
                100, 53, 101, 52, 57, 53, 51, 98, 97, 97, 98, 52, 48, 98, 55, 54, 97, 100, 101, 98,
                54, 51, 52, 50, 97, 99, 48, 0, 0
              });

      UserStatus expectedStatus = new UserStatus();
      UserStatus actualStatus = client.readUserStatus(stream);

      expectedStatus.setAction(Status.PLAYING);
      expectedStatus.setText("Mutsuhiko Izumi - Red Goose [Rainbow Beginner]");
      expectedStatus.setMods(new ArrayList<>());
      expectedStatus.setBeatmapChecksum("18ab0d5e4953baab40b76adeb6342ac0");

      assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Message")
    public void testMessage() throws IOException {
      stream = new ByteArrayInputStream(new byte[] {11, 5, 33, 104, 101, 108, 112});

      Message expectedMessage = new Message();
      Message actualMessage = client.readMessage(stream);

      expectedMessage.setSender("");
      expectedMessage.setContent("!help");
      expectedMessage.setTarget("#osu");

      assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("SpectateFrames")
    public void testSpectateFrames() throws IOException {
      stream =
          new ByteArrayInputStream(
              new byte[] {
                30, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 0, 2, 0, 0, 0, 0, -46, -20, 44, 67,
                -120, 47, 61, 67, 16, 2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 37, 2, 0,
                0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 48, 2, 0, 0, 0, 0, -46, -20, 44, 67,
                -120, 47, 61, 67, 69, 2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 85, 2, 0,
                0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 96, 2, 0, 0, 0, 0, -46, -20, 44, 67,
                -120, 47, 61, 67, 117, 2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -117, 2,
                0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -107, 2, 0, 0, 0, 0, -46, -20, 44,
                67, -120, 47, 61, 67, -91, 2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -69,
                2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -59, 2, 0, 0, 0, 0, -46, -20, 44,
                67, -120, 47, 61, 67, -37, 2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -16,
                2, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, -11, 2, 0, 0, 0, 0, -46, -20, 44,
                67, -120, 47, 61, 67, 11, 3, 0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 32, 3,
                0, 0, 0, 0, -46, -20, 44, 67, -120, 47, 61, 67, 43, 3, 0, 0, 0, 0, -100, -84, 43,
                67, 109, -113, 60, 67, 64, 3, 0, 0, 0, 0, 103, 108, 42, 67, 82, -17, 59, 67, 80, 3,
                0, 0, 0, 0, 76, -52, 41, 67, 82, -17, 59, 67, 91, 3, 0, 0, 0, 0, 76, -52, 41, 67,
                82, -17, 59, 67, 112, 3, 0, 0, 0, 0, 76, -52, 41, 67, 82, -17, 59, 67, -123, 3, 0,
                0, 0, 0, 76, -52, 41, 67, 82, -17, 59, 67, -112, 3, 0, 0, 0, 0, 76, -52, 41, 67, 82,
                -17, 59, 67, -96, 3, 0, 0, 0, 0, 76, -52, 41, 67, 82, -17, 59, 67, -75, 3, 0, 0, 0,
                0, 50, 44, 41, 67, 82, -17, 59, 67, -64, 3, 0, 0, 0, 0, 23, -116, 40, 67, 56, 79,
                59, 67, -43, 3, 0, 0, 0, 0, -4, -21, 39, 67, 56, 79, 59, 67, -27, 3, 0, 0, 0
              });

      ReplayFrameBundle expectedBundle = new ReplayFrameBundle();
      ReplayFrameBundle actualBundle = client.readSpectateFrames(stream);

      expectedBundle.setAction(ReplayAction.STANDARD);
      expectedBundle.setFrames(
          List.of(
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 512),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 528),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 549),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 560),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 581),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 597),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 608),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 629),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 651),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 661),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 677),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 699),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 709),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 731),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 752),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 757),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 779),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 800),
              new ReplayFrame(0, 172.92507934570312, 189.1856689453125, 811),
              new ReplayFrame(0, 171.67425537109375, 188.5602569580078, 832),
              new ReplayFrame(0, 170.42344665527344, 187.93484497070312, 848),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 859),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 880),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 901),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 912),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 928),
              new ReplayFrame(0, 169.79803466796875, 187.93484497070312, 949),
              new ReplayFrame(0, 169.17263793945312, 187.93484497070312, 960),
              new ReplayFrame(0, 168.54722595214844, 187.3094482421875, 981),
              new ReplayFrame(0, 167.92181396484375, 187.3094482421875, 997)));

      assertEquals(expectedBundle, actualBundle);
    }
  }
}

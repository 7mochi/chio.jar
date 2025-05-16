package pe.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.nanamochi.io.IBanchoIO;
import pe.nanamochi.objects.*;
import pe.nanamochi.objects.enums.*;
import pe.nanamochi.utils.ClientManager;

@DisplayName("B282 Client Tests")
public class B282Test {

  static IBanchoIO client;
  ByteArrayOutputStream stream;

  @BeforeAll
  static void setupClient() {
    client = ClientManager.getClient(282);
  }

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
  @DisplayName("Message packet (#osu channel)")
  public void testMessagePacketToOsuChannel() throws IOException {
    client.writeMessage(stream, new Message("test_username", "test_message", "#osu", 3));

    assertArrayEquals(
        new byte[] {
          7, 0, 45, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119, 47,
          45, 78, 45, -54, 75, -52, 77, -27, -26, 1, 115, 115, 83, -117, -117, 19, -45, 83, 1, -42,
          20, -90, 3, 29, 0, 0, 0
        },
        stream.toByteArray());
  }

  @Test
  @DisplayName("Message packet (Unsupported channel)")
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37, 7, -92,
          67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81, 36, 51,
          39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
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
            new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30, -105,
          90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109,
          -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119, 47,
          45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
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
                new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0, 1, 100)),
            QuitState.GONE));

    assertArrayEquals(
        new byte[] {
          13, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30, -105,
          90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109,
          -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
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
                new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0, 1, 100)),
            QuitState.IRC_REMAINING));

    assertArrayEquals(
        new byte[] {
          13, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119, 47,
          45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
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
                new ReplayFrame(ButtonState.RIGHT_1, 0.0, 0.0, 0),
                new ReplayFrame(ButtonState.LEFT_2, 0.0, 0.0, 1),
                new ReplayFrame(ButtonState.SMOKE, 0.0, 0.0, 2)),
            new ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5, 0),
            0));

    assertArrayEquals(
        new byte[] {
          16, 0, 33, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 100, 64, 2, -116,
          88, 88, 32, -64, 4, 34, 0, -61, 103, 87, -106, 45, 0, 0, 0
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37, 7, -92,
          67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81, 36, 51,
          39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
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
            new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30, -105,
          90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109,
          -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119, 47,
          45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 90, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, -127, -101, -59, 51, 37, 7, -92,
          67, -104, 91, -60, 47, -75, 36, 35, -75, 40, 39, 49, 47, -91, 88, -63, 49, -81, 36, 51,
          39, 39, -75, 24, 0, 90, 80, -125, -78, 95, 0, 0, 0
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
            new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30, -105,
          90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109,
          -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0
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
            new UserStatus(Status.IDLE, "Idle", Mods.NoMod, Mode.OSU, "", 0, false),
            new UserStats(1, 0, 0, 100.0, 1, 100)));

    assertArrayEquals(
        new byte[] {
          11, 0, 35, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119, 47,
          45, 78, 45, -54, 75, -52, 77, 5, 0, -31, -98, -108, -36, 15, 0, 0, 0
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
                new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
                new UserStats(1, 0, 0, 100.0, 1, 100)),
            new UserInfo(
                4,
                "test_username_2",
                new UserPresence(false, -5, 10, Permissions.REGULAR, 0.0, 0.0, ""),
                new UserStatus(Status.LOBBY, "", Mods.NoMod, Mode.OSU, "", 0, false),
                new UserStats(2, 0, 0, 99.0, 3, 50))));

    assertArrayEquals(
        new byte[] {
          12, 0, 85, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, -32, -26, 45, 73,
          45, 46, -119, 47, 45, 78, 45, -54, 75, -52, 77, 101, 64, 1, -111, 14, -116, 72, 60, 16,
          -101, -101, -45, 56, -34, -64, -64, 64, -81, 32, 47, -99, 11, 36, 38, -52, 45, -30, -105,
          90, -110, -111, 90, -108, -109, -104, -105, 82, -84, -32, -104, 87, -110, -103, -109,
          -109, 90, 12, 0, 35, -126, -70, -114, 90, 0, 0, 0, 12, 0, 88, 0, 0, 0, 31, -117, 8, 0, 0,
          0, 0, 0, 2, -1, 99, 97, 96, 96, -32, -26, 47, 73, 45, 46, -119, 47, 45, 78, 45, -54, 75,
          -52, 77, -115, 55, 98, 64, 6, 7, 34, 28, -104, -111, -72, 76, 32, 13, -100, 38, -15, 6, 6,
          6, 122, 5, 121, -23, 92, 32, 49, 97, 110, 17, -65, -44, -110, -116, -44, -94, -100, -60,
          -68, -108, 98, 5, -57, -68, -110, -52, -100, -100, -44, 98, 0, -86, -86, 15, 20, 92, 0, 0,
          0
        },
        stream.toByteArray());
  }
}

package pe.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;
import pe.nanamochi.clients.B298;
import pe.nanamochi.objects.*;
import pe.nanamochi.objects.enums.Mods;
import pe.nanamochi.utils.ClientManager;

@DisplayName("B298 Client Tests")
public class B298Test {

  static B298 client;

  @BeforeAll
  static void setupClient() {
    client = (B298) ClientManager.getClient(298);
  }

  @Nested
  class WritePackets {

    ByteArrayOutputStream stream;

    @BeforeEach
    void setupOutputStream() {
      stream = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("MatchUpdate")
    public void testMatchUpdatePacket() throws IOException {
      client.writeMatchUpdate(
          stream,
          new Match(
              1,
              false,
              0,
              0,
              "Test multiplayer",
              "123",
              "Test - Test",
              0,
              "",
              Arrays.asList(
                  new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD)),
                  new MatchSlot(
                      2, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, List.of(Mods.HARD_ROCK)),
                  new MatchSlot(
                      3,
                      SlotStatus.HAS_PLAYER,
                      SlotTeam.NEUTRAL,
                      Arrays.asList(Mods.HIDDEN, Mods.HARD_ROCK))),
              2,
              0,
              0,
              0,
              false,
              0));

      assertArrayEquals(
          new byte[] {
            27, 0, 58, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
            99, 100, -32, 22, 8, 73, 45, 46, 81, -56, 45, -51, 41, -55, 44, -56,
            73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128, 40, 6, 16, 96, 100,
            99, 96, 2, 82, -52, 64, 12, 0, 63, -28, 75, 27, 49, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("MatchNew")
    public void testMatchNewPacket() throws IOException {
      client.writeMatchNew(
          stream,
          new Match(
              2,
              true,
              0,
              522171579,
              "Test multiplayer",
              "123",
              "Test - Test",
              0,
              "",
              Arrays.asList(
                  new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD)),
                  new MatchSlot(
                      2,
                      SlotStatus.HAS_PLAYER,
                      SlotTeam.NEUTRAL,
                      List.of(Mods.HIDDEN, Mods.HARD_ROCK)),
                  new MatchSlot(
                      3, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, List.of(Mods.FLASHLIGHT))),
              3,
              0,
              0,
              0,
              true,
              0));

      assertArrayEquals(
          new byte[] {
            28, 0, 58, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 98, -32, 22, 8, 73, 45, 46,
            81, -56, 45, -51, 41, -55, 44, -56, 73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128,
            40, 6, 16, 96, 100, 99, 96, 2, 82, -52, 64, 12, 0, 75, -109, -46, 81, 49, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("MatchDisband")
    public void testMatchDisbandPacket() throws IOException {
      client.writeMatchDisband(stream, 3);

      assertArrayEquals(
          new byte[] {
            29, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("LobbyJoin")
    public void testLobbyJoinPacket() throws IOException {
      client.writeLobbyJoin(stream, 3);

      assertArrayEquals(
          new byte[] {
            35, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("LobbyPart")
    public void testLobbyPartPacket() throws IOException {
      client.writeLobbyPart(stream, 4);

      assertArrayEquals(
          new byte[] {
            36, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 97, 96, 96, 0, 0, 75, 72, 38,
            -82, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("MatchJoinSuccess")
    public void testMatchJoinSuccessPacket() throws IOException {
      client.writeMatchJoinSuccess(
          stream,
          new Match(
              3,
              false,
              0,
              0,
              "Test multiplayer",
              "123",
              "Test - Test",
              0,
              "",
              Arrays.asList(
                  new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD)),
                  new MatchSlot(2, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD)),
                  new MatchSlot(3, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD)),
                  new MatchSlot(4, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, List.of(Mods.NO_MOD))),
              3,
              0,
              0,
              0,
              false,
              0));

      assertArrayEquals(
          new byte[] {
            37, 0, 60, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, -32, 22, 8, 73, 45, 46,
            81, -56, 45, -51, 41, -55, 44, -56, 73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128,
            40, 6, 16, 96, -28, 99, 96, 2, 82, -52, 64, -52, 2, -60, 0, 86, -7, -123, -1, 53, 0, 0,
            0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("MatchJoinFail")
    public void testMatchJoinFailPacket() throws IOException {
      client.writeMatchJoinFail(stream);

      assertArrayEquals(
          new byte[] {
            38, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("FellowSpectatorJoined")
    public void testFellowSpectatorJoinedPacket() throws IOException {
      client.writeFellowSpectatorJoined(stream, 3);

      assertArrayEquals(
          new byte[] {
            43, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
            -15, 51, 4, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("FellowSpectatorLeft")
    public void testFellowSpectatorLeftPacket() throws IOException {
      client.writeFellowSpectatorLeft(stream, 5);

      assertArrayEquals(
          new byte[] {
            44, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 101, 96, 96, 0, 0, 46, 47,
            -102, 22, 4, 0, 0, 0
          },
          stream.toByteArray());
    }
  }

  @Nested
  class ReadPackets {

    ByteArrayInputStream stream;

    @Test
    @DisplayName("ReadMatch")
    public void testReadMatch() throws IOException {
      Match expectedMatch = new Match();
      // TODO: Add real match data
      // Match actualMatch = client.readMatch(stream);
      Match actualMatch = new Match();

      assertEquals(expectedMatch, actualMatch);
    }
  }
}

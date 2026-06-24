package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.MatchSlot;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.Mode;
import io.github.nanamochi.chio.model.enums.Mods;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.ScoringType;
import io.github.nanamochi.chio.model.enums.SlotStatus;
import io.github.nanamochi.chio.model.enums.SlotTeam;
import io.github.nanamochi.chio.model.enums.TeamType;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("B298 Client Tests")
public class B298Test {

  static B298 client;

  @BeforeAll
  static void setupClient() {
    client = (B298) ClientSelector.selectClient(298);
  }

  @Test
  @DisplayName("MatchUpdate")
  public void testMatchUpdatePacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          27, 0, 59, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 100, -32, 22, 8, 73, 45, 46,
          81, -56, 45, -51, 41, -55, 44, -56, 73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128, 40,
          6, 16, 96, 100, 99, 96, 2, 82, -52, 12, 12, 12, 0, 63, -28, 75, 27, 49, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_UPDATE,
            new Match(
                1,
                false,
                MatchType.STANDARD,
                0,
                "Test multiplayer",
                "123",
                "Test - Test",
                0,
                "",
                Arrays.asList(
                    new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(
                        2, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, Mods.HARD_ROCK.getValue()),
                    new MatchSlot(
                        3,
                        SlotStatus.HAS_PLAYER,
                        SlotTeam.NEUTRAL,
                        Mods.HIDDEN.getValue() | Mods.HARD_ROCK.getValue())),
                2,
                Mode.OSU,
                ScoringType.SCORE,
                TeamType.HEAD_TO_HEAD,
                false,
                0)));
  }

  @Test
  @DisplayName("MatchNew")
  public void testMatchNewPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          28, 0, 59, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 98, -32, 22, 8, 73, 45, 46, 81,
          -56, 45, -51, 41, -55, 44, -56, 73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128, 40, 6,
          16, 96, 100, 99, 96, 2, 82, -52, 12, 12, 12, 0, 75, -109, -46, 81, 49, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_NEW,
            new Match(
                2,
                true,
                MatchType.STANDARD,
                522171579,
                "Test multiplayer",
                "123",
                "Test - Test",
                0,
                "",
                Arrays.asList(
                    new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(
                        2,
                        SlotStatus.HAS_PLAYER,
                        SlotTeam.NEUTRAL,
                        Mods.HIDDEN.getValue() | Mods.HARD_ROCK.getValue()),
                    new MatchSlot(
                        3, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, Mods.FLASHLIGHT.getValue())),
                3,
                Mode.OSU,
                ScoringType.SCORE,
                TeamType.HEAD_TO_HEAD,
                true,
                0)));
  }

  @Test
  @DisplayName("MatchDisband")
  public void testMatchDisbandPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          29, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
          -15, 51, 4, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_MATCH_DISBAND, 3));
  }

  @Test
  @DisplayName("LobbyJoin")
  public void testLobbyJoinPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          35, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
          -15, 51, 4, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_LOBBY_JOIN, 3));
  }

  @Test
  @DisplayName("LobbyPart")
  public void testLobbyPartPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          36, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 97, 96, 96, 0, 0, 75, 72, 38,
          -82, 4, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_LOBBY_PART, 4));
  }

  @Test
  @DisplayName("MatchJoinSuccess")
  public void testMatchJoinSuccessPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          37, 0, 62, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, -32, 22, 8, 73, 45, 46,
          81, -56, 45, -51, 41, -55, 44, -56, 73, -84, 76, 45, -30, -26, 6, -117, -24, 42, -128, 40,
          6, 16, 96, -28, 99, 96, 2, 82, -52, 64, -52, -62, -64, -64, 0, 0, 86, -7, -123, -1, 53, 0,
          0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_JOIN_SUCCESS,
            new Match(
                3,
                false,
                MatchType.STANDARD,
                0,
                "Test multiplayer",
                "123",
                "Test - Test",
                0,
                "",
                Arrays.asList(
                    new MatchSlot(1, SlotStatus.OPEN, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(2, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(3, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(4, SlotStatus.HAS_PLAYER, SlotTeam.NEUTRAL, 0)),
                3,
                Mode.OSU,
                ScoringType.SCORE,
                TeamType.HEAD_TO_HEAD,
                false,
                0)));
  }

  @Test
  @DisplayName("MatchJoinFail")
  public void testMatchJoinFailPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          38, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_MATCH_JOIN_FAIL));
  }

  @Test
  @DisplayName("FellowSpectatorJoined")
  public void testFellowSpectatorJoinedPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          43, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 0, 0, -14, 112,
          -15, 51, 4, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_FELLOW_SPECTATOR_JOINED, 3));
  }

  @Test
  @DisplayName("FellowSpectatorLeft")
  public void testFellowSpectatorLeftPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          44, 0, 24, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 101, 96, 96, 0, 0, 46, 47,
          -102, 22, 4, 0, 0, 0
        },
        client.writePacket(PacketType.BANCHO_FELLOW_SPECTATOR_LEFT, 5));
  }
}

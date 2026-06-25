package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.MatchSlot;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.Mode;
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

@DisplayName("B334 Client Tests")
public class B334Test {

  static B334 client;

  @BeforeAll
  static void setupClient() {
    client = (B334) ClientSelector.selectClient(334);
  }

  @Test
  @DisplayName("MatchTransferHost")
  public void testMatchTransferHostPacket() throws IOException {
    assertArrayEquals(
        new byte[] {52, 0, 0, 0, 0, 0, 0},
        client.writePacket(PacketType.BANCHO_MATCH_TRANSFER_HOST));
  }

  @Test
  @DisplayName("MatchAllPlayersLoaded")
  public void testMatchAllPlayersLoadedPacket() throws IOException {
    assertArrayEquals(
        new byte[] {55, 0, 0, 0, 0, 0, 0},
        client.writePacket(PacketType.BANCHO_MATCH_ALL_PLAYERS_LOADED));
  }

  @Test
  @DisplayName("MatchPlayerFailed")
  public void testMatchPlayerFailedPacket() throws IOException {
    assertArrayEquals(
        new byte[] {59, 0, 0, 4, 0, 0, 0, 3, 0, 0, 0},
        client.writePacket(PacketType.BANCHO_MATCH_PLAYER_FAILED, 3));
  }

  @Test
  @DisplayName("MatchComplete")
  public void testMatchCompletePacket() throws IOException {
    assertArrayEquals(
        new byte[] {60, 0, 0, 0, 0, 0, 0}, client.writePacket(PacketType.BANCHO_MATCH_COMPLETE));
  }

  @Test
  @DisplayName("MatchStart (with match data)")
  public void testMatchStartPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          47, 0, 0, 32, 0, 0, 0, 1, 0, 0, 0, 0, 11, 4, 84, 101,
          115, 116, 11, 3, 49, 50, 51, 0, 0, 0, 0, 0, 1, 4, 8, 2,
          0, 0, 0, 3, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_START,
            new Match(
                1,
                false,
                MatchType.STANDARD,
                0,
                "Test",
                "",
                "123",
                0,
                "",
                Arrays.asList(
                    new MatchSlot(0, SlotStatus.OPEN, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(2, SlotStatus.NOT_READY, SlotTeam.NEUTRAL, 0),
                    new MatchSlot(3, SlotStatus.READY, SlotTeam.NEUTRAL, 0)),
                1,
                Mode.OSU,
                ScoringType.SCORE,
                TeamType.HEAD_TO_HEAD,
                false,
                0)));
  }

  @Test
  @DisplayName("MatchScoreUpdate (without checksum)")
  public void testMatchScoreUpdatePacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          49, 0, 0, 27, 0, 0, 0, 10, 0, 0, 0, 0, 1, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, -72, 11, 0, 0, 1, 0, 0, 0,
          0, 5
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_SCORE_UPDATE,
            new ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5, 0)));
  }
}

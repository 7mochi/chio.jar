package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.Mode;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.ScoringType;
import io.github.nanamochi.chio.model.enums.TeamType;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("B312 Client Tests")
public class B312Test {

  static B312 client;

  @BeforeAll
  static void setupClient() {
    client = (B312) ClientSelector.selectClient(312);
  }

  @Test
  @DisplayName("MatchStart")
  public void testMatchStartPacket() throws IOException {
    assertArrayEquals(
        new byte[] {
          46, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_START,
            new Match(
                0,
                false,
                MatchType.STANDARD,
                0,
                null,
                null,
                null,
                0,
                null,
                new ArrayList<>(),
                0,
                Mode.OSU,
                ScoringType.SCORE,
                TeamType.HEAD_TO_HEAD,
                false,
                0)));
  }

  @Test
  @DisplayName("MatchScoreUpdate")
  public void testMatchScoreUpdate() throws IOException {
    assertArrayEquals(
        new byte[] {
          48, 0, 77, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, 86, -80, 76, -75, 52, 51, 74,
          52, 72, 53, -75, 76, -79, 52, 72, 76, 74, -74, 52, 55, 73, -77, 52, -77, 76, -76, 48, 50,
          51, 49, 74, 78, -76, -28, 98, 0, 2, 30, 6, 22, 6, 70, 6, 102, 32, 102, 98, -40, 32, -59,
          -64, -64, 11, 100, 49, -80, 2, 0, -127, -120, -116, 97, 61, 0, 0, 0
        },
        client.writePacket(
            PacketType.BANCHO_MATCH_SCORE_UPDATE,
            new ScoreFrame(10, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, 5, 0)));
  }
}

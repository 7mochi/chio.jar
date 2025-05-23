package pe.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.*;
import pe.nanamochi.clients.B312;
import pe.nanamochi.objects.Match;
import pe.nanamochi.objects.ScoreFrame;
import pe.nanamochi.utils.ClientManager;

@DisplayName("B312 Client Tests")
public class B312Test {

  static B312 client;

  @BeforeAll
  static void setupClient() {
    client = (B312) ClientManager.getClient(312);
  }

  @Nested
  class WritePackets {

    ByteArrayOutputStream stream;

    @BeforeEach
    void setupOutputStream() {
      stream = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("MatchStart")
    public void testMatchStartPacket() throws IOException {
      client.writeMatchStart(stream, new Match());

      assertArrayEquals(
          new byte[] {
            46, 0, 20, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("MatchScoreUpdate")
    public void testMatchScoreUpdate() throws IOException {
      client.writeMatchScoreUpdate(
          stream, new ScoreFrame(10, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, 5, 0));

      assertArrayEquals(
          new byte[] {
            48, 0, 77, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, 86, -80, 76, -75, 52, 51,
            74, 52, 72, 53, -75, 76, -79, 52, 72, 76, 74, -74, 52, 55, 73, -77, 52, -77, 76, -76,
            48, 50, 51, 49, 74, 78, -76, -28, 98, 0, 2, 30, 6, 22, 6, 70, 6, 102, 32, 102, 98, -40,
            32, -59, -64, -64, 11, 100, 49, -80, 2, 0, -127, -120, -116, 97, 61, 0, 0, 0
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

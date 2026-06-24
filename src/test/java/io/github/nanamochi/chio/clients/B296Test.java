package io.github.nanamochi.chio.clients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.nanamochi.chio.ClientSelector;
import io.github.nanamochi.chio.model.ReplayFrame;
import io.github.nanamochi.chio.model.ReplayFrameBundle;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.ButtonState;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.ReplayAction;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("B296 Client Tests")
public class B296Test {

  static B296 client;

  @BeforeAll
  static void setupClient() {
    client = (B296) ClientSelector.selectClient(296);
  }

  @Nested
  class WritePackets {

    @Test
    @DisplayName("SpectateFrames")
    public void testSpectateFramesPacket() throws IOException {
      assertArrayEquals(
          new byte[] {
            16, 0, 75, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1,
            99, 102, 96, 96, 100, 64, 2, -116, 88, 88, 32, -64, 4, 34, -72, 21,
            82, 83, -115, -109, 45, 77, 82, 18, -109, 18, 45, 13, -110, -115, -52, -45,
            76, -116, -109, 82, -51, 83, 83, 77, -109, -109, 76, -116, 82, 82, 45, -52,
            -72, -48, 117, -18, -32, -122, -14, 89, 1, -25, 18, 31, 9, 106, 0, 0,
            0
          },
          client.writePacket(
              PacketType.BANCHO_SPECTATE_FRAMES,
              new ReplayFrameBundle(
                  ReplayAction.STANDARD,
                  List.of(
                      new ReplayFrame(ButtonState.RIGHT_1.getValue(), 0.0f, 0.0f, 0),
                      new ReplayFrame(ButtonState.LEFT_2.getValue(), 0.0f, 0.0f, 1),
                      new ReplayFrame(ButtonState.SMOKE.getValue(), 0.0f, 0.0f, 2)),
                  new ScoreFrame(10, 0, 1, 0, 0, 0, 0, 0, 3000, 1, 0, false, 5, 0),
                  0)));
    }
  }

  @Nested
  class ReadPackets {

    @Test
    @DisplayName("SpectateFrames")
    public void testSpectateFrames() throws IOException {
      byte[] data =
          new byte[] {
            30, 0, 0, 0, -47, -25, -66, 67, 52, 59, -93, 66, 0, 45, 0, 0, 0, 0, -60, -105, -66, 67,
            -108, 122, -97, 66, 11, 45, 0, 0, 0, 0, -100, -89, -67, 67, -76, 56, -108, 66, 43, 45,
            0, 0, 0, 0, -114, 87, -67, 67, 73, -72, -111, 66, 48, 45, 0, 0, 0, 0, 116, -73, -68, 67,
            20, 120, -112, 66, 59, 45, 0, 0, 0, 0, 116, -73, -68, 67, -34, 55, -113, 66, 80, 45, 0,
            0, 0, 0, 102, 103, -68, 67, -87, -9, -115, 66, 101, 45, 0, 0, 0, 0, 102, 103, -68, 67,
            -87, -9, -115, 66, 123, 45, 0, 0, 0, 0, 89, 23, -68, 67, 116, -73, -116, 66, -128, 45,
            0, 0, 0, 0, 62, 119, -69, 67, 116, -73, -116, 66, -117, 45, 0, 0, 0, 1, -4, -26, -71,
            67, 62, 119, -117, 66, -85, 45, 0, 0, 0, 1, -31, 70, -71, 67, 62, 119, -117, 66, -75,
            45, 0, 0, 0, 1, -85, 6, -72, 67, 62, 119, -117, 66, -53, 45, 0, 0, 0, 1, 105, 118, -74,
            67, 62, 119, -117, 66, -48, 45, 0, 0, 0, 1, 121, -43, -80, 67, 62, 119, -117, 66, -16,
            45, 0, 0, 0, 1, 83, -12, -87, 67, 62, 119, -117, 66, 5, 46, 0, 0, 0, 1, 83, -12, -87,
            67, 62, 119, -117, 66, 5, 46, 0, 0, 0, 1, -104, -109, -91, 67, 62, 119, -117, 66, 27,
            46, 0, 0, 0, 1, 32, -61, -94, 67, 62, 119, -117, 66, 32, 46, 0, 0, 0, 1, -88, -14, -97,
            67, 62, 119, -117, 66, 75, 46, 0, 0, 0, 1, -88, -14, -97, 67, 62, 119, -117, 66, 75, 46,
            0, 0, 0, 1, -128, 2, -97, 67, 116, -73, -116, 66, 85, 46, 0, 0, 0, 1, 21, -126, -100,
            67, 116, -73, -116, 66, 112, 46, 0, 0, 0, 1, -59, -95, -102, 67, 116, -73, -116, 66,
            123, 46, 0, 0, 0, 1, 117, -63, -104, 67, 116, -73, -116, 66, -112, 46, 0, 0, 0, 1, 24,
            -111, -106, 67, 116, -73, -116, 66, -91, 46, 0, 0, 0, 1, -29, 80, -107, 67, 116, -73,
            -116, 66, -69, 46, 0, 0, 0, 1, -96, -64, -109, 67, 116, -73, -116, 66, -64, 46, 0, 0, 0,
            1, 13, 80, -112, 67, 116, -73, -116, 66, -53, 46, 0, 0, 0, 1, 56, 79, -117, 67, 116,
            -73, -116, 66, -32, 46, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 0, 4, 0, 1, 0, 3, 0, 1, 0, 2, 0,
            -80, 26, 0, 0, 13, 0, 1, 0, 0, -88
          };

      ReplayFrameBundle expected = new ReplayFrameBundle();
      ReplayFrameBundle actual = client.readSpectateFrames(data);

      expected.setAction(ReplayAction.STANDARD);
      expected.setFrames(
          List.of(
              new ReplayFrame(0, 381.8110656738281f, 81.61563110351562f, 11520),
              new ReplayFrame(0, 381.1856689453125f, 79.73941040039062f, 11531),
              new ReplayFrame(0, 379.3094482421875f, 74.11074829101562f, 11563),
              new ReplayFrame(0, 378.68402099609375f, 72.85993194580078f, 11568),
              new ReplayFrame(0, 377.4332275390625f, 72.23452758789062f, 11579),
              new ReplayFrame(0, 377.4332275390625f, 71.60911560058594f, 11600),
              new ReplayFrame(0, 376.80780029296875f, 70.98371124267578f, 11621),
              new ReplayFrame(0, 376.80780029296875f, 70.98371124267578f, 11643),
              new ReplayFrame(0, 376.1824035644531f, 70.35830688476562f, 11648),
              new ReplayFrame(0, 374.93157958984375f, 70.35830688476562f, 11659),
              new ReplayFrame(2, 371.8045654296875f, 69.73289489746094f, 11691),
              new ReplayFrame(2, 370.5537414550781f, 69.73289489746094f, 11701),
              new ReplayFrame(2, 368.0520935058594f, 69.73289489746094f, 11723),
              new ReplayFrame(2, 364.9250793457031f, 69.73289489746094f, 11728),
              new ReplayFrame(2, 353.6677551269531f, 69.73289489746094f, 11760),
              new ReplayFrame(2, 339.9087829589844f, 69.73289489746094f, 11781),
              new ReplayFrame(2, 339.9087829589844f, 69.73289489746094f, 11781),
              new ReplayFrame(2, 331.153076171875f, 69.73289489746094f, 11803),
              new ReplayFrame(2, 325.5244140625f, 69.73289489746094f, 11808),
              new ReplayFrame(2, 319.895751953125f, 69.73289489746094f, 11851),
              new ReplayFrame(2, 319.895751953125f, 69.73289489746094f, 11851),
              new ReplayFrame(2, 318.01953125f, 70.35830688476562f, 11861),
              new ReplayFrame(2, 313.0162658691406f, 70.35830688476562f, 11888),
              new ReplayFrame(2, 309.2638244628906f, 70.35830688476562f, 11899),
              new ReplayFrame(2, 305.5113830566406f, 70.35830688476562f, 11920),
              new ReplayFrame(2, 301.133544921875f, 70.35830688476562f, 11941),
              new ReplayFrame(2, 298.6319274902344f, 70.35830688476562f, 11963),
              new ReplayFrame(2, 295.5048828125f, 70.35830688476562f, 11968),
              new ReplayFrame(2, 288.6253967285156f, 70.35830688476562f, 11979),
              new ReplayFrame(2, 278.618896484375f, 70.35830688476562f, 12000)));

      expected.setFrame(new ScoreFrame(0, 0, 12, 4, 1, 3, 1, 2, 6832, 13, 1, false, -88, 0));
      assertEquals(expected, actual);
    }
  }
}

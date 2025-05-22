package pe.nanamochi.chio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.*;
import pe.nanamochi.clients.B294;
import pe.nanamochi.objects.Message;
import pe.nanamochi.objects.ReplayFrame;
import pe.nanamochi.objects.ReplayFrameBundle;
import pe.nanamochi.objects.ScoreFrame;
import pe.nanamochi.objects.enums.ButtonState;
import pe.nanamochi.objects.enums.ReplayAction;
import pe.nanamochi.utils.ClientManager;

@DisplayName("B294 Client Tests")
public class B294Test {

  static B294 client;

  @BeforeAll
  static void setupClient() {
    client = (B294) ClientManager.getClient(294);
  }

  @Nested
  class WritePackets {

    ByteArrayOutputStream stream;

    @BeforeEach
    void setupOutputStream() {
      stream = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("Message (#osu channel)")
    public void testMessagePacketToOsuChannel() throws IOException {
      client.writeMessage(stream, new Message("test_username", "test_message", "#osu", 3));

      assertArrayEquals(
          new byte[] {
            7, 0, 46, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, -27, -26, 1, 115, 115, 83, -117, -117, 19, -45, 83,
            25, 0, 120, 62, -79, -67, 30, 0, 0, 0
          },
          stream.toByteArray());
    }

    @Test
    @DisplayName("Message (Private message)")
    public void testMessagePacketToAnotherUser() throws IOException {
      client.writeMessage(
          stream, new Message("test_username", "test_message", "test_username_2", 3));

      assertArrayEquals(
          new byte[] {
            7, 0, 46, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, -29, -26, 45, 73, 45, 46, -119,
            47, 45, 78, 45, -54, 75, -52, 77, -27, -26, 1, 115, 115, 83, -117, -117, 19, -45, 83,
            25, 1, -18, 14, -74, -54, 30, 0, 0, 0
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
            16, 0, 75, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 2, -1, 99, 102, 96, 96, 100, 64, 2,
            -116, 88, 88, 32, -64, 4, 34, -72, 21, 82, 83, -115, -109, 45, 77, 82, 18, -109, 18, 45,
            13, -110, -115, -52, -45, 76, -116, -109, 82, -51, 83, 83, 77, -109, -109, 76, -116, 82,
            82, 45, -52, 80, 116, -19, -32, -122, -102, -62, 10, 0, 106, -78, -36, 4, 102, 0, 0, 0
          },
          stream.toByteArray());
    }
  }

  @Nested
  class ReadPackets {

    ByteArrayInputStream stream;

    @Test
    @DisplayName("PrivateMessage")
    public void testMessage() throws IOException {
      stream = new ByteArrayInputStream(new byte[] {11, 4, 115, 104, 105, 116});

      Message expectedMessage = new Message();
      Message actualMessage = client.readMessage(stream);

      expectedMessage.setSender("");
      expectedMessage.setContent("shit");
      expectedMessage.setTarget("#osu");

      assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("SpectateFrames")
    public void testSpectateFrames() throws IOException {
      stream =
          new ByteArrayInputStream(
              new byte[] {
                30, 0, 0, 0, 120, -48, -110, 67, -108, 122, -97, 66, 11, 0, 0, 0, 0, 0, -40, 15,
                -113, 67, -34, 55, -113, 66, 21, 0, 0, 0, 0, 0, -118, 62, -121, 67, 60, 104, 113,
                66, 53, 0, 0, 0, 0, 0, -92, -39, 121, 67, 16, 100, 88, 66, 64, 0, 0, 0, 0, 0, -29,
                85, 99, 67, -49, -35, 50, 66, 80, 0, 0, 0, 0, 0, -72, 81, 74, 67, 36, -41, 10, 66,
                91, 0, 0, 0, 0, 0, 120, -48, 66, 67, 27, -91, -34, 65, 123, 0, 0, 0, 0, 0, 40, -16,
                64, 67, 112, -93, -44, 65, -123, 0, 0, 0, 0, 0, -40, 15, 63, 67, -59, -95, -54, 65,
                -112, 0, 0, 0, 0, 0, -94, -49, 61, 67, -59, -95, -54, 65, -96, 0, 0, 0, 0, 0, 109,
                -113, 60, 67, -16, -96, -59, 65, -75, 0, 0, 0, 0, 0, -24, 110, 57, 67, 112, -93,
                -44, 65, -53, 0, 0, 0, 0, 0, 98, 78, 54, 67, -100, -89, -19, 65, -43, 0, 0, 0, 0, 0,
                -9, -51, 51, 67, 70, -87, -9, 65, -32, 0, 0, 0, 0, 0, 34, -51, 46, 67, -7, -41, 15,
                66, -5, 0, 0, 0, 0, 0, 50, 44, 41, 67, 36, -36, 40, 66, 5, 1, 0, 0, 0, 0, 12, 75,
                34, 67, 37, -31, 70, 66, 27, 1, 0, 0, 0, 0, -105, -119, 25, 67, 38, -26, 100, 66,
                37, 1, 0, 0, 0, 0, 1, 10, -4, 66, 51, 54, -123, 66, 64, 1, 0, 0, 0, 0, -10, -61,
                -41, 66, -98, -74, -121, 66, 75, 1, 0, 0, 0, 0, 64, -127, -57, 66, -98, -74, -121,
                66, 85, 1, 0, 0, 0, 0, 11, 65, -58, 66, -98, -74, -121, 66, 107, 1, 0, 0, 0, 0, 11,
                65, -58, 66, -98, -74, -121, 66, -128, 1, 0, 0, 0, 1, 64, -127, -57, 66, -98, -74,
                -121, 66, -112, 1, 0, 0, 0, 1, 64, -127, -57, 66, -98, -74, -121, 66, -101, 1, 0, 0,
                0, 1, 64, -127, -57, 66, 105, 118, -122, 66, -91, 1, 0, 0, 0, 1, 64, -127, -57, 66,
                105, 118, -122, 66, -59, 1, 0, 0, 0, 0, 64, -127, -57, 66, 105, 118, -122, 66, -48,
                1, 0, 0, 0, 0, 64, -127, -57, 66, 105, 118, -122, 66, -32, 1, 0, 0, 0, 0, 64, -127,
                -57, 66, 105, 118, -122, 66, -21, 1, 0, 0, 0
              });

      ReplayFrameBundle expectedBundle = new ReplayFrameBundle();
      ReplayFrameBundle actualBundle = client.readSpectateFrames(stream);

      expectedBundle.setAction(ReplayAction.STANDARD);
      expectedBundle.setFrames(
          List.of(
              new ReplayFrame(0, 293.628662109375, 79.73941040039062, 11),
              new ReplayFrame(0, 286.123779296875, 71.60911560058594, 21),
              new ReplayFrame(0, 270.48858642578125, 60.35179138183594, 53),
              new ReplayFrame(0, 249.85015869140625, 54.09771728515625, 64),
              new ReplayFrame(0, 227.3354949951172, 44.716609954833984, 80),
              new ReplayFrame(0, 202.3192138671875, 34.71009826660156, 91),
              new ReplayFrame(0, 194.8143310546875, 27.830617904663086, 123),
              new ReplayFrame(0, 192.9381103515625, 26.579803466796875, 133),
              new ReplayFrame(0, 191.0618896484375, 25.328989028930664, 144),
              new ReplayFrame(0, 189.81106567382812, 25.328989028930664, 160),
              new ReplayFrame(0, 188.5602569580078, 24.703582763671875, 181),
              new ReplayFrame(0, 185.4332275390625, 26.579803466796875, 203),
              new ReplayFrame(0, 182.30618286132812, 29.70684051513672, 213),
              new ReplayFrame(0, 179.80455017089844, 30.957653045654297, 224),
              new ReplayFrame(0, 174.80130004882812, 35.96091079711914, 251),
              new ReplayFrame(0, 169.17263793945312, 42.21498107910156, 261),
              new ReplayFrame(0, 162.29315185546875, 49.71986770629883, 283),
              new ReplayFrame(0, 153.53746032714844, 57.224754333496094, 293),
              new ReplayFrame(0, 126.01953887939453, 66.6058578491211, 320),
              new ReplayFrame(0, 107.88273620605469, 67.85667419433594, 331),
              new ReplayFrame(0, 99.75244140625, 67.85667419433594, 341),
              new ReplayFrame(0, 99.12703704833984, 67.85667419433594, 363),
              new ReplayFrame(0, 99.12703704833984, 67.85667419433594, 384),
              new ReplayFrame(2, 99.75244140625, 67.85667419433594, 400),
              new ReplayFrame(2, 99.75244140625, 67.85667419433594, 411),
              new ReplayFrame(2, 99.75244140625, 67.23126983642578, 421),
              new ReplayFrame(2, 99.75244140625, 67.23126983642578, 453),
              new ReplayFrame(0, 99.75244140625, 67.23126983642578, 464),
              new ReplayFrame(0, 99.75244140625, 67.23126983642578, 480),
              new ReplayFrame(0, 99.75244140625, 67.23126983642578, 491)));
      assertEquals(expectedBundle, actualBundle);
    }
  }
}

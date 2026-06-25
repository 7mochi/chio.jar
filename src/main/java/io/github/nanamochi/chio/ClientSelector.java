package io.github.nanamochi.chio;

import io.github.nanamochi.chio.clients.B282;
import io.github.nanamochi.chio.clients.B291;
import io.github.nanamochi.chio.clients.B294;
import io.github.nanamochi.chio.clients.B296;
import io.github.nanamochi.chio.clients.B298;
import io.github.nanamochi.chio.clients.B312;
import io.github.nanamochi.chio.clients.B320;
import io.github.nanamochi.chio.clients.B323;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public final class ClientSelector {
  private static final NavigableMap<Integer, Supplier<BanchoIO>> CLIENTS = new TreeMap<>();
  private static final int MIN_VERSION;
  private static final int MAX_VERSION;

  static {
    CLIENTS.put(282, B282::new);
    CLIENTS.put(291, B291::new);
    CLIENTS.put(294, B294::new);
    CLIENTS.put(296, B296::new);
    CLIENTS.put(298, B298::new);
    CLIENTS.put(312, B312::new);
    CLIENTS.put(320, B320::new);
    CLIENTS.put(323, B323::new);

    MIN_VERSION = CLIENTS.firstKey();
    MAX_VERSION = CLIENTS.lastKey();
  }

  private ClientSelector() {}

  public static BanchoIO selectClient(int version) {
    Supplier<BanchoIO> factory = CLIENTS.get(version);
    if (factory != null) return factory.get();

    if (version < MIN_VERSION) return CLIENTS.get(MIN_VERSION).get();
    if (version > MAX_VERSION) return CLIENTS.get(MAX_VERSION).get();

    Integer floorKey = CLIENTS.floorKey(version);
    return CLIENTS.get(floorKey != null ? floorKey : MIN_VERSION).get();
  }

  public static BanchoIO selectLatestClient() {
    return CLIENTS.get(MAX_VERSION).get();
  }

  public static BanchoIO selectInitialClient() {
    return CLIENTS.get(MIN_VERSION).get();
  }
}

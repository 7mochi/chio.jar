package pe.nanamochi.utils;

import java.lang.reflect.Constructor;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import pe.nanamochi.io.IBanchoIO;

public class ClientManager {

  private static final NavigableMap<Integer, Function<Integer, IBanchoIO>> versionedFactories =
      new TreeMap<>();

  static {
    loadClientFactories();
  }

  private ClientManager() {}

  private static void loadClientFactories() {
    System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "warn");

    try {
      Reflections reflections =
          new Reflections("pe.nanamochi.clients", Scanners.TypesAnnotated, Scanners.SubTypes);

      Set<Class<? extends IBanchoIO>> clientClasses = reflections.getSubTypesOf(IBanchoIO.class);

      for (Class<? extends IBanchoIO> clazz : clientClasses) {
        String simpleName = clazz.getSimpleName();
        Integer version = extractVersion(simpleName);
        if (version == null) continue;

        Function<Integer, IBanchoIO> factory = (v) -> createInstance(clazz, 8, 0);

        versionedFactories.put(version, factory);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load client versions", e);
    }
  }

  private static Integer extractVersion(String className) {
    if (!className.startsWith("B")) return null;
    try {
      return Integer.parseInt(className.substring(1));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static IBanchoIO createInstance(
      Class<? extends IBanchoIO> clazz, int slotSize, int protocolVersion) {
    try {
      Constructor<? extends IBanchoIO> ctor = clazz.getConstructor(int.class, int.class);
      return ctor.newInstance(slotSize, protocolVersion);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create instance of " + clazz.getSimpleName(), e);
    }
  }

  public static IBanchoIO getClient(int version) {
    if (versionedFactories.isEmpty()) {
      throw new IllegalStateException("No client versions registered");
    }

    int minVersion = versionedFactories.firstKey();
    int maxVersion = versionedFactories.lastKey();

    if (version < minVersion) {
      return versionedFactories.get(minVersion).apply(version);
    }

    if (version > maxVersion) {
      return versionedFactories.get(maxVersion).apply(version);
    }

    if (versionedFactories.containsKey(version)) {
      return versionedFactories.get(version).apply(version);
    }

    Integer floorKey = versionedFactories.floorKey(version);
    if (floorKey != null) {
      return versionedFactories.get(floorKey).apply(version);
    }

    throw new IllegalArgumentException("Unsupported version: " + version);
  }
}

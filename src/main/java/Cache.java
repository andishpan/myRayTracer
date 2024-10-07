import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static final Map<String, Integer> cache = new ConcurrentHashMap<>();

    public static void put(String key, int value) {
        cache.put(key, value);
    }

    public static Integer get(String key) {
        return cache.get(key);
    }

    public static void invalidateQuadric(Quadric quadric) {
        String quadricHash = String.valueOf(quadric.hashCode());
        System.out.println("Invalidating cache entries for quadric hash: " + quadricHash);
        cache.keySet().removeIf(key -> key.contains(quadricHash));
    }

    public static void invalidateAll() {
        cache.clear();
    }
}

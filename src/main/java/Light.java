import java.util.Random;

public class Light {
    public Vector3f position;
    public Vector3f intensity;
    public Vector3f direction;
    public float radius;
    private static final Random random = new Random();

    public Light(Vector3f position, Vector3f intensity, Vector3f direction, float radius) {
        this.position = position;
        this.intensity = intensity;
        this.direction = direction.normalize();
        this.radius = radius;
    }

    public int applyAreaFalloff(int color, Vector3f intersectionPoint) {
        float distance = intersectionPoint.subtract(position).length();
        float falloffFactor = 1.0f;

        if (distance > radius) {
            falloffFactor = Math.max(0.1f, 1.0f - (distance - radius) / radius);
        }

        int red = (int) (((color >> 16) & 0xFF) * falloffFactor);
        int green = (int) (((color >> 8) & 0xFF) * falloffFactor);
        int blue = (int) ((color & 0xFF) * falloffFactor);

        return (red << 16) | (green << 8) | blue;
    }

    public Vector3f randomPoint() {
        float x = (random.nextFloat() - 0.5f) * radius;
        float y = (random.nextFloat() - 0.5f) * radius;
        float z = (random.nextFloat() - 0.5f) * radius;
        return position.add(new Vector3f(x, y, z));
    }
}

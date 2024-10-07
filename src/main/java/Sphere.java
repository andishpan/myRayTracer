public class Sphere extends Quadric {
    private Vector3f center;
    private float radius;

    public Sphere(Vector3f center, float radius, int color,String name) {
        super(1, 1, 1, 0, 0, 0, -2 * center.x, -2 * center.y, -2 * center.z,
                center.x * center.x + center.y * center.y + center.z * center.z - radius * radius, color,name);
        this.center = center;
        this.radius = radius;
    }

    @Override
    public float intersects(Ray ray) {
        Vector3f p = ray.origin;
        Vector3f v = ray.direction;

        float px = p.x, py = p.y, pz = p.z;
        float vx = v.x, vy = v.y, vz = v.z;

        float A = a * vx * vx + b * vy * vy + c * vz * vz +
                2 * (d * vx * vy + e * vx * vz + f * vy * vz);
        float B = 2 * (a * px * vx + b * py * vy + c * pz * vz +
                d * (px * vy + py * vx) +
                e * (px * vz + pz * vx) +
                f * (py * vz + pz * vy)) +
                g * vx + h * vy + i * vz;
        float C = a * px * px + b * py * py + c * pz * pz +
                2 * (d * px * py + e * px * pz + f * py * pz) +
                g * px + h * py + i * pz + j;

        float discriminant = B * B - 4 * A * C;
        if (discriminant < 0) {
            return -1.0f;
        }

        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-B - sqrtDiscriminant) / (2.0f * A);
        float t2 = (-B + sqrtDiscriminant) / (2.0f * A);

        if (t1 > 0 && t2 > 0) {
            return Math.min(t1, t2);
        } else if (t1 > 0) {
            return t1;
        } else if (t2 > 0) {
            return t2;
        } else {
            return -1.0f;
        }
    }

    @Override
    public AABB getBoundingBox() {
        return new AABB(
                center.subtract(new Vector3f(radius, radius, radius)),
                center.add(new Vector3f(radius, radius, radius))
        );
    }
}

public class Plane extends Quadric {
    private Vector3f normal;
    private Vector3f position;
    private float d;

    public Plane(Vector3f normal, Vector3f position, float d, int color,String name) {
        super(0, 0, 0, 0, 0, 0, normal.x, normal.y, normal.z, -d, color,name);
        this.normal = normal.normalize();
        this.position = position;
        this.d = d;
    }

    @Override
    public float intersects(Ray ray) {
        float denom = normal.dot(ray.direction);
        if (Math.abs(denom) > 1e-6) {
            float t = (normal.dot(ray.origin.subtract(position))) / denom;
            return t >= 0 ? t : -1;
        }
        return -1;
    }

    @Override
    public Vector3f getNormal(Vector3f point) {
        return normal;
    }

    public int getColor(Vector3f point) {
        int checkerSize = 300;
        int x = (int) Math.floor(point.x / checkerSize);
        int y = (int) Math.floor(point.z / checkerSize);
        if ((x + y) % 2 == 0) {
            return 0xFFFFFF;
        } else {
            return 0x000000;
        }
    }

    @Override
    public AABB getBoundingBox() {

        float scale = 5000;

        if (Math.abs(normal.y) > 0.000001f) {

            return new AABB(
                    new Vector3f(-scale, position.y  / 2, -scale),
                    new Vector3f(scale, position.y  / 2, scale)
            );
        } else if (Math.abs(normal.x) > 0.000001f) {

            return new AABB(
                    new Vector3f(position.x  / 2, -scale, -scale),
                    new Vector3f(position.x  / 2, scale, scale)
            );
        } else {

            return new AABB(
                    new Vector3f(-scale, -scale, position.z  / 2),
                    new Vector3f(scale, scale, position.z  / 2)
            );
        }
    }
}

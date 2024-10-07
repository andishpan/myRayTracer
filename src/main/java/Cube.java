public class Cube extends Quadric {
    private Vector3f minCorner;
    private Vector3f maxCorner;

    public Cube(Vector3f center, float sizeX, float sizeY, float sizeZ, int color,String name) {
        super(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, color,name);
        setPosition(center, sizeX, sizeY, sizeZ);
    }

    public void setPosition(Vector3f center, float sizeX, float sizeY, float sizeZ) {
        this.minCorner = center.subtract(new Vector3f(sizeX / 2, sizeY / 2, sizeZ / 2));
        this.maxCorner = center.add(new Vector3f(sizeX / 2, sizeY / 2, sizeZ / 2));
    }

    @Override
    public float intersects(Ray ray) {
        float tmin = (minCorner.x - ray.origin.x) / ray.direction.x;
        float tmax = (maxCorner.x - ray.origin.x) / ray.direction.x;

        if (tmin > tmax) {
            float temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        float tymin = (minCorner.y - ray.origin.y) / ray.direction.y;
        float tymax = (maxCorner.y - ray.origin.y) / ray.direction.y;

        if (tymin > tymax) {
            float temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return -1.0f;
        }

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        float tzmin = (minCorner.z - ray.origin.z) / ray.direction.z;
        float tzmax = (maxCorner.z - ray.origin.z) / ray.direction.z;

        if (tzmin > tzmax) {
            float temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return -1.0f;
        }

        if (tzmin > tmin) {
            tmin = tzmin;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        if (tmin < 0 && tmax < 0) {
            return -1.0f;
        }

        return tmin > 0 ? tmin : tmax;
    }

    @Override
    public AABB getBoundingBox() {
        return new AABB(minCorner, maxCorner);
    }

    @Override
    public Vector3f getNormal(Vector3f point) {
        //world to local coord
        Vector3f center = minCorner.add(maxCorner).scale(0.5f);
        Vector3f p = point.subtract(center);
        float threshold = 0.001f;

        if (Math.abs(p.x - (maxCorner.x - center.x)) < threshold) {
            return new Vector3f(1, 0, 0);
        } else if (Math.abs(p.x - (minCorner.x - center.x)) < threshold) {
            return new Vector3f(-1, 0, 0);
        } else if (Math.abs(p.y - (maxCorner.y - center.y)) < threshold) {
            return new Vector3f(0, 1, 0);
        } else if (Math.abs(p.y - (minCorner.y - center.y)) < threshold) {
            return new Vector3f(0, -1, 0);
        } else if (Math.abs(p.z - (maxCorner.z - center.z)) < threshold) {
            return new Vector3f(0, 0, 1);
        } else if (Math.abs(p.z - (minCorner.z - center.z)) < threshold) {
            return new Vector3f(0, 0, -1);
        } else {
            return new Vector3f(0, 0, 0);
        }
    }
}

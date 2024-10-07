public class AABB {
    // min max corners of Bounding box
    public Vector3f min;
    public Vector3f max;

    public AABB() {
        this.min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        this.max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public boolean intersect(Ray ray) {
        float tmin = (min.x - ray.origin.x) / ray.direction.x;
        float tmax = (max.x - ray.origin.x) / ray.direction.x;

        if (tmin > tmax) {
            float temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        float tymin = (min.y - ray.origin.y) / ray.direction.y;
        float tymax = (max.y - ray.origin.y) / ray.direction.y;

        if (tymin > tymax) {
            float temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        float tzmin = (min.z - ray.origin.z) / ray.direction.z;
        float tzmax = (max.z - ray.origin.z) / ray.direction.z;

        if (tzmin > tzmax) {
            float temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }

        return true;
    }

    // Bounding box Vereinigung
    public AABB union(AABB other) {
        return new AABB(
                new Vector3f(
                        Math.min(this.min.x, other.min.x),
                        Math.min(this.min.y, other.min.y),
                        Math.min(this.min.z, other.min.z)
                ),
                new Vector3f(
                        Math.max(this.max.x, other.max.x),
                        Math.max(this.max.y, other.max.y),
                        Math.max(this.max.z, other.max.z)
                )
        );
    }

    public Vector3f getCenter() {
        return new Vector3f(
                (min.x + max.x) / 2,
                (min.y + max.y) / 2,
                (min.z + max.z) / 2
        );
    }

    public int getLongestAxis() {
        float xLength = max.x - min.x;
        float yLength = max.y - min.y;
        float zLength = max.z - min.z;

        if (xLength > yLength && xLength > zLength) {
            return 0; // x-axis
        } else if (yLength > zLength) {
            return 1; // y-axis
        } else {
            return 2; // z-axis
        }
    }

    @Override
    public String toString() {
        return "AABB{" + "min=" + min + ", max=" + max + '}';
    }
}

public class Ray {
    Vector3f origin;
    Vector3f direction;
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3f getPoint(float t) {
        return origin.add(direction.multiply(t));
    }
}


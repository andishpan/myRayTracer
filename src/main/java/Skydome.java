public class Skydome extends Quadric {
    private Texture texture;

    public Skydome(Vector3f center, float radius, Texture texture,String name) {

        super(1, 1, 1, 0, 0, 0, -2 * center.x, -2 * center.y, -2 * center.z,
                center.x * center.x + center.y * center.y + center.z * center.z - radius * radius, 0xFFFFFF,name);
        this.texture = texture;
        this.center = center;
    }

    @Override
    public int getShadedColor(Vector3f direction, Vector3f normal, Light light, Camera camera, int shadowLevel, int numShadowSamples) {
        if (texture == null) {
            System.out.println("Sky texture is null");
            return 0x000000;
        }

        direction = direction.normalize();

        // Use only the upper hemisphere
     /*   if (direction.y < 0) {
            return 0x000000;
        }*/

        // vector to texture coordinates
        float u = (float) (0.5 + Math.atan2(direction.z, direction.x) / (2 * Math.PI));
        // flipped v coordinate
        float v = (float) (0.5 + Math.asin(direction.y) / Math.PI);

        int textureColor = texture.getColor(u, v);
        Vector3f baseColorVector = Vector3f.intToVector3f(textureColor);

        return Vector3f.vector3fToInt(baseColorVector);
    }

    @Override
    public float intersects(Ray ray) {
        float t = super.intersects(ray);
        if (t >= 0) {
            Vector3f intersectionPoint = ray.origin.add(ray.direction.scale(t));

            if (intersectionPoint.y >= center.y) {
                return t;
            }
        }
        return -1;
    }

    @Override
    public AABB getBoundingBox() {

        return new AABB(new Vector3f(Float.NEGATIVE_INFINITY, center.y, Float.NEGATIVE_INFINITY),
                new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }

}


public class Cylinder extends Quadric {
    private Vector3f center;
    private float radius;
    private float height;
    private int axis;

    private int color;

    public Cylinder(Vector3f center, float radius, float height, int axis, int color,String name) {
        super(axis == 0 ? 0 : 1, axis == 1 ? 0 : 1, axis == 2 ? 0 : 1, 0, 0, 0, 0, 0, 0, -radius * radius, color,name);
        this.center = center;
        this.radius = radius;
        this.height = height;
        this.axis = axis;
        this.color = color;
    }

    @Override
    public AABB getBoundingBox() {
        Vector3f min, max;
        switch (axis) {
            case 0: // x-axis
                min = new Vector3f(center.x - height / 2, center.y - radius, center.z - radius);
                max = new Vector3f(center.x + height / 2, center.y + radius, center.z + radius);
                break;
            case 1: // y-axis
                min = new Vector3f(center.x - radius, center.y - height / 2, center.z - radius);
                max = new Vector3f(center.x + radius, center.y + height / 2, center.z + radius);
                break;
            case 2: // z-axis
                min = new Vector3f(center.x - radius, center.y - radius, center.z - height / 2);
                max = new Vector3f(center.x + radius, center.y + radius, center.z + height / 2);
                break;
            default:
                throw new IllegalArgumentException("Invalid axis");
        }
        return new AABB(min, max);
    }
}

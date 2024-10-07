public class IntersectionInfo {
    public Quadric closestQuadric;
    public Vector3f intersectionPoint;
    public Vector3f normal;
    public float closestT;

    public IntersectionInfo() {
        this.closestQuadric = null;
        this.intersectionPoint = new Vector3f(0, 0, 0);
        this.normal = new Vector3f(0, 0, 0);
        this.closestT = Float.MAX_VALUE;
    }
}

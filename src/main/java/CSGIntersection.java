

/*public class CSGIntersection extends Quadric {
    private Quadric quadricA;
    private Quadric quadricB;
    private Quadric lastIntersectedQuadric;


    public CSGIntersection(Quadric quadricA, Quadric quadricB) {
        super(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.quadricA = quadricA;
        this.quadricB = quadricB;
    }

    @Override
    public float intersects(Ray ray) {
        float tA = quadricA.intersects(ray);
        float tB = quadricB.intersects(ray);

        if (tA < 0 || tB < 0) {
            return -1.0f;
        }


        float enterA = Math.min(tA, tB);
        float exitA = Math.max(tA, tB);

        float t = (enterA >= 0 && enterA <= exitA) ? enterA : -1.0f;
        lastIntersectedQuadric = (t == tA) ? quadricA : quadricB;

        return t;
    }

    @Override
    public Vector3f getNormal(Vector3f point) {
        if (lastIntersectedQuadric != null) {
            return lastIntersectedQuadric.getNormal(point);
        }
        return new Vector3f(0, 0, 0);
    }


   /* public int getShadedColor(Vector3f intersectionPoint, Vector3f normal, Light light, Camera camera, boolean inShadow) {
        if (lastIntersectedQuadric != null) {

            return lastIntersectedQuadric.getShadedColor(intersectionPoint, normal, light,camera, inShadow) ;
        }
        return 0x000000;
    } */
//}

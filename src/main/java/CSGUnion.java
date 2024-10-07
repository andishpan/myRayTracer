/*public class CSGUnion extends Quadric {
    private Quadric quadricA;
    private Quadric quadricB;
    private Quadric lastIntersectedQuadric;

    public CSGUnion(Quadric quadricA, Quadric quadricB) {
        super(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.quadricA = quadricA;
        this.quadricB = quadricB;
    }

    @Override
    public float intersects(Ray ray) {
        float tA = quadricA.intersects(ray);
        float tB = quadricB.intersects(ray);
        float closestT = Float.MAX_VALUE;

        if (tA >= 0 && tA < closestT) {
            closestT = tA;
            lastIntersectedQuadric = quadricA;
        }
        if (tB >= 0 && tB < closestT) {
            closestT = tB;
            lastIntersectedQuadric = quadricB;
        }

        if (closestT == Float.MAX_VALUE) {
            return -1.0f;
        } else {
            return closestT;
        }

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
            return lastIntersectedQuadric.getShadedColor(intersectionPoint, normal, light, camera, inShadow);
        }
        return 0x000000;
    }*/





//}

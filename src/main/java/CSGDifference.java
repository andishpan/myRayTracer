/*import java.util.Arrays;

public class CSGDifference extends Quadric {
    private Quadric quadricA;
    private Quadric quadricB;
    private Quadric lastIntersectedQuadric;

    public CSGDifference(Quadric quadricA, Quadric quadricB) {
        super(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.quadricA = quadricA;
        this.quadricB = quadricB;
    }


    @Override
    public float intersects(Ray ray) {

        float tAin = quadricA.intersects(ray);
        float tAout;
        if (tAin >= 0) {
            tAout = quadricA.intersects(new Ray(ray.origin.add(ray.direction.scale(1.0f)), ray.direction));
        } else {
            tAout = -1.0f;
        }


        float tBin = quadricB.intersects(ray);
        float tBout;
        if (tBin >= 0) {
            tBout = quadricB.intersects(new Ray(ray.origin.add(ray.direction.scale(1.0f)), ray.direction));
        } else {
            tBout = -1.0f;
        }



        float[] tValues = {tAin, tAout, tBin, tBout};
        Arrays.sort(tValues);

        boolean insideA = false;
        boolean insideB = false;
        float closestIntersection = -1.0f;

        for (float t : tValues) {
            if (t < 0) continue;

            Vector3f point = ray.origin.add(ray.direction.scale(t));
            boolean pointInsideA = quadricA.contains(point);
            boolean pointInsideB = quadricB.contains(point);

            if (t == tAin) {
                insideA = true;
                if (!pointInsideB) {
                    lastIntersectedQuadric = quadricA;
                    closestIntersection = t;
                }
            } else if (t == tBin) {

               if(tAin > tBin){

                       lastIntersectedQuadric = quadricB;
                   if (closestIntersection == -1.0f || t < closestIntersection) {
                       closestIntersection = t;
                   }


               }else if(tBout > tBin){


                   lastIntersectedQuadric = quadricB;
                   if (closestIntersection == -1.0f || t < closestIntersection) {
                       closestIntersection = t;
                   }
               }


            }
        }

        return closestIntersection;
    }

    @Override
    public Vector3f getNormal(Vector3f point) {
        if (lastIntersectedQuadric != null) {
            return lastIntersectedQuadric.getNormal(point);
        }

        // normale umdrehen
        if(lastIntersectedQuadric != null && lastIntersectedQuadric == quadricB){
            return lastIntersectedQuadric.getNormal(point);
        }
        return new Vector3f(0, 0, 0);
    }


    /*public int getShadedColor(Vector3f intersectionPoint, Vector3f normal, Light light, Camera camera, boolean inShadow) {
        if (lastIntersectedQuadric != null) {
            return lastIntersectedQuadric.getShadedColor(intersectionPoint, normal, light, camera, inShadow);
        }
        return 0x000000;
    } */
//}
//normale von B quadrics umdrehen
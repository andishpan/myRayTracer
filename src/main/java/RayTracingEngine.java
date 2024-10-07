import java.util.Random;

public class RayTracingEngine {
    private static final int MAX_DEPTH = 5;
    protected static final int SHADOW_SAMPLES = 100;
    static Random random = new Random();

    public static int traceRay(Scene scene, Ray ray, Light light, Camera camera, int depth, boolean useBVH) {
        if (depth > MAX_DEPTH) {
            return 0x000000;
        }

        IntersectionInfo intersectionInfo = new IntersectionInfo();

        if (useBVH) {
            findClosestIntersection(scene.getBVH().getRoot(), ray, intersectionInfo);
        } else {
            findClosestIntersectionWithoutBVH(scene, ray, intersectionInfo);
        }

        if (intersectionInfo.closestQuadric != null && intersectionInfo.closestT > 0) {
            int shadowLevel = intersectionInfo.closestQuadric.getShadows(intersectionInfo.intersectionPoint, light, scene, SHADOW_SAMPLES);
            int localColor = intersectionInfo.closestQuadric.getShadedColor(intersectionInfo.intersectionPoint, intersectionInfo.normal, light, camera, shadowLevel, SHADOW_SAMPLES);

            if (shadowLevel < SHADOW_SAMPLES) {


                Vector3f reflectedDirection = ray.direction.subtract(intersectionInfo.normal.scale(2 * ray.direction.dot(intersectionInfo.normal))).normalize();
                Ray reflectedRay = new Ray(intersectionInfo.intersectionPoint.add(intersectionInfo.normal.scale(0.005f)), reflectedDirection);
                int reflectedColor = traceRay(scene, reflectedRay, light, camera, depth + 1, useBVH);

                if (intersectionInfo.closestQuadric.transparency > 0 && intersectionInfo.closestQuadric.refractionIndex > 0) {
                    Vector3f refractedDirection = snellRefraction(ray.direction, intersectionInfo.normal, 1.0f, intersectionInfo.closestQuadric.refractionIndex);
                    if (refractedDirection != null) {
                        Ray refractedRay = new Ray(intersectionInfo.intersectionPoint.add(intersectionInfo.normal.scale(-0.005f)), refractedDirection);
                        int refractedColor = traceRay(scene, refractedRay, light, camera, depth + 1, useBVH);
                        return combineColors(localColor, reflectedColor, refractedColor, intersectionInfo.closestQuadric.reflection, intersectionInfo.closestQuadric.transparency);
                    }
                }
                return combineColors(localColor, reflectedColor, 0, intersectionInfo.closestQuadric.reflection, 0);
            } else {
                return localColor;
            }
        } else {
            if (scene.getSkydome() != null) {
                return scene.getSkydome().getShadedColor(ray.direction.scale(1000), null, light, camera, 0, 0);
            }
            return 0x000000;
        }
    }

    public static int traceRayWithJittering(Scene scene, Light light, Camera camera, int x, int y, boolean useBVH) {
        int numSamples = 4;
        int[] colors = new int[numSamples];
        int index = 0;

        for (int i = 0; i < numSamples; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2; //  [-1, 1]
            double offsetY = (random.nextDouble() - 0.5) * 2; //  [-1, 1]
            Ray ray = camera.generateRay(x + offsetX, y + offsetY);
            colors[index++] = traceRay(scene, ray, light, camera, 1, useBVH);
        }

        return finalColor(colors);
    }

    private static void findClosestIntersection(BVHNode node, Ray ray, IntersectionInfo intersectionInfo) {
        if (node == null || !node.boundingBox.intersect(ray)) {
            return;
        }

        if (node.isLeaf()) {
            for (Quadric obj : node.objects) {
                float t = obj.intersects(ray);
                if (t >= 0 && t < intersectionInfo.closestT) {
                    intersectionInfo.closestT = t;
                    intersectionInfo.closestQuadric = obj;
                    intersectionInfo.intersectionPoint.set(ray.origin.add(ray.direction.scale(t)));
                    intersectionInfo.normal.set(obj.getNormal(intersectionInfo.intersectionPoint));
                }
            }
        } else {
            //if not leaf recursion
            findClosestIntersection(node.left, ray, intersectionInfo);
            findClosestIntersection(node.right, ray, intersectionInfo);
        }
    }


    public static int traceRayWithSuperSampling(Scene scene, Light light, Camera camera, int x, int y, boolean useBVH) {

        Ray ray1 = camera.generateRay(x + 0.25, y + 0.25);
        Ray ray2 = camera.generateRay(x + 0.75, y + 0.75);
        int color1 = traceRay(scene, ray1, light, camera, 1, useBVH);
        int color2 = traceRay(scene, ray2, light, camera, 1, useBVH);

        // if similar return average
        if (areColorsSimilar(color1, color2)) {
            return finalColor(new int[]{color1, color2});
        }
//else 2 more rays

        Ray ray3 = camera.generateRay(x + 0.25, y + 0.75);
        Ray ray4 = camera.generateRay(x + 0.75, y + 0.25);
        int color3 = traceRay(scene, ray3, light, camera, 1, useBVH);
        int color4 = traceRay(scene, ray4, light, camera, 1, useBVH);

        return finalColor(new int[]{color1, color2, color3, color4});
    }


    public static int traceRayWithEnhancedSuperSampling(Scene scene, Light light, Camera camera, int x, int y, boolean useBVH) {
        int numSamples = 4; // Anzahl der Supersampling-Strahlen
        int[] colors = new int[numSamples * numSamples];
        int index = 0;

        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numSamples; j++) {
                double offsetX = (i + 0.5) / numSamples;
                double offsetY = (j + 0.5) / numSamples;
                Ray ray = camera.generateRay(x + offsetX, y + offsetY);
                colors[index++] = traceRay(scene, ray, light, camera, 1, useBVH);
            }
        }

        return finalColor(colors);
    }

    private static boolean areColorsSimilar(int color1, int color2) {
        int threshold = 10;


        int red1 = (color1 >> 16) & 0xFF;
        int green1 = (color1 >> 8) & 0xFF;
        int blue1 = color1 & 0xFF;


        int red2 = (color2 >> 16) & 0xFF;
        int green2 = (color2 >> 8) & 0xFF;
        int blue2 = color2 & 0xFF;


        int redDifference = Math.abs(red1 - red2);
        int greenDifference = Math.abs(green1 - green2);
        int blueDifference = Math.abs(blue1 - blue2);


        if (redDifference < threshold && greenDifference < threshold && blueDifference < threshold) {
            return true;
        } else {
            return false;
        }
    }


    private static int finalColor(int[] colors) {
        int r = 0, g = 0, b = 0;
        for (int color : colors) {
            r += (color >> 16) & 0xFF;
            g += (color >> 8) & 0xFF;
            b += color & 0xFF;
        }
        int numColors = colors.length;
        r /= numColors;
        g /= numColors;
        b /= numColors;
        return (r << 16) | (g << 8) | b;
    }



    private static Vector3f snellRefraction(Vector3f v1, Vector3f n, float refractiveIndexObj1, float refractiveIndexObj2) {
        float i = refractiveIndexObj1 / refractiveIndexObj2;
        float a = Math.max(-1.0f, Math.min(1.0f, v1.dot(n)));

        if (a < 0) {
            a = -a;
        } else {
            n = n.scale(-1);
            float temp = refractiveIndexObj1;
            refractiveIndexObj1 = refractiveIndexObj2;
            refractiveIndexObj2 = temp;
            i = refractiveIndexObj1 / refractiveIndexObj2;
        }

        float k = 1 - i * i * (1 - a * a);
        if (k < 0) {
            return null;
        } else {
            float b = (float) Math.sqrt(k);
            return v1.scale(i).add(n.scale(i * a - b)).normalize();
        }
    }

    private static int combineColors(int localColor, int reflectedColor, int refractedColor, float reflectivity, float transparency) {
        float localWeight = 1.0f - reflectivity - transparency;
        float reflectedWeight = reflectivity;
        float refractedWeight = transparency;

        int r = (int) ((localWeight * ((localColor >> 16) & 0xFF) +
                reflectedWeight * ((reflectedColor >> 16) & 0xFF) +
                refractedWeight * ((refractedColor >> 16) & 0xFF)));
        int g = (int) ((localWeight * ((localColor >> 8) & 0xFF) +
                reflectedWeight * ((reflectedColor >> 8) & 0xFF) +
                refractedWeight * ((refractedColor >> 8) & 0xFF)));
        int b = (int) ((localWeight * (localColor & 0xFF) +
                reflectedWeight * (reflectedColor & 0xFF) +
                refractedWeight * (refractedColor & 0xFF)));
        return (r << 16) | (g << 8) | b;
    }


    private static void findClosestIntersectionWithoutBVH(Scene scene, Ray ray, IntersectionInfo intersectionInfo) {
        for (Quadric q : scene.getQuadrics()) {
            float t = q.intersects(ray);
            if (t >= 0 && t < intersectionInfo.closestT) {
                intersectionInfo.closestT = t;
                intersectionInfo.closestQuadric = q;
                intersectionInfo.intersectionPoint.set(ray.origin.add(ray.direction.scale(t)));
                intersectionInfo.normal.set(q.getNormal(intersectionInfo.intersectionPoint));
            }
        }
    }
}

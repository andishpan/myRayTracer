import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RayTracer {
    private static final int RES_X = 1024;
    private static final int RES_Y = 768;
    private static final double FOV = Math.PI / 3;
    private static int[] pixels;
    private static MemoryImageSource mis;

    private static boolean useBVH = true;
    private static Quadric sphere4;
    private static Quadric sphere5;
    private static Quadric sphere6;

    public static void main(String[] args) {
        setupDisplay();
        Scene scene = createScene();

        double angle = Math.toRadians(-5);
        Vector3f cameraPosition = new Vector3f(0, -400, 800);
        Vector3f cameraDirection = new Vector3f(0.0f, (float) Math.sin(angle), -(float) Math.cos(angle)).normalize();

        Camera camera = new Camera(cameraPosition, cameraDirection, FOV, RES_X, RES_Y);


        Light light = new Light(new Vector3f(-800, 800, 0), new Vector3f(5, 5, 5), new Vector3f(-1, -1, -1), 200.0f);

        renderLoop(scene, camera, light);
    }

    private static void setupDisplay() {
        pixels = new int[RES_X * RES_Y];
        mis = new MemoryImageSource(RES_X, RES_Y, new DirectColorModel(24, 0xff0000, 0xff00, 0xff), pixels, 0, RES_X);
        mis.setAnimated(true);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);
        JFrame frame = new JFrame("Ray Tracing");
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static Scene createScene() {
        Scene scene = new Scene();
        Texture skyTexture = null;
        try {
            skyTexture = new Texture("C:\\RayTracer\\MyRayTracer\\src\\main\\resources\\Sky.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Skydome skydome = new Skydome(new Vector3f(0, 0, 0), 10000, skyTexture,"skydome");
        scene.addQuadric(skydome);

        Cube cube = new Cube(new Vector3f(550, -300, -600), 200,200,200, 0x800080,"cube");
        cube.setReflection(0.8f);
        cube.setTransparency(0.0f);
        cube.setRefractionIndex(0.0f);
        float zPlane = -1000;
       // Sphere sphere1 = new Sphere(new Vector3f(550, -412, -400), 80, 0xFF5733,"newSphere");
      // sphere1.setReflection(0.0f);
       // sphere1.setTransparency(0.4f);
       // sphere1.setRefractionIndex(1.5f);
       /* Cylinder cylinder1 = new Cylinder(new Vector3f(412, -312, -850), 150, 450, 1, 0x37f341);
        cylinder1.setReflection(0.2f);
        cylinder1.setTransparency(0.0f);
        cylinder1.setRefractionIndex(1.3f); */

        //gelb
        Sphere sphere2 = new Sphere(new Vector3f(-212, -300, -900), 90, 0xFFFF00,"gelb");
        sphere2.setReflection(0.3f);
        sphere2.setTransparency(0.0f);
        sphere2.setRefractionIndex(1.3f);

        // light blue
        Sphere sphere3 = new Sphere(new Vector3f(0, -300, -1200), 150, 0xADD8E6,"sphere3");
        sphere3.setReflection(0.4f);
        sphere3.setTransparency(0.0f);
        sphere3.setRefractionIndex(1.4f);

        // red
        sphere4 = new Sphere(new Vector3f(0, -500, -900), 90, 0xFF0000,"red");
        sphere4.setReflection(0.2f);
        sphere4.setTransparency(0.0f);
        sphere4.setRefractionIndex(1.2f);

        // blue
        sphere5 = new Sphere(new Vector3f(200, -400, -600), 150, 0x0000FF,"blue");
        sphere5.setReflection(0.5f);
        sphere5.setTransparency(0.0f);
        sphere5.setRefractionIndex(1.3f);

        // pink
        sphere6 = new Sphere(new Vector3f(-512, -300, -700), 150, 0xFF69B4,"pink");
        sphere6.setReflection(0.3f);
        sphere6.setTransparency(0.0f);
        sphere6.setRefractionIndex(1.3f);

        Plane checkerboard = new Plane(new Vector3f(0, 1, 0), new Vector3f(0, 0, -200), -100, 0xFFFFFF,"checkboard");
        checkerboard.setReflection(0.3f);
        checkerboard.setTransparency(0.0f);

       //  scene.addQuadric(sphere1);
        scene.addQuadric(cube);

        scene.addQuadric(skydome);
        // scene.addQuadric(roundedBox);
        scene.addQuadric(sphere2);
        scene.addQuadric(sphere3);
        // red
        scene.addQuadric(sphere4);
        // blue
        scene.addQuadric(sphere5);
        scene.addQuadric(sphere6);
         scene.addQuadric(checkerboard);
        scene.buildBVH();


        return scene;
    }

    private static void renderLoop(Scene scene, Camera camera, Light light) {

        while (true) {
            long startTime = System.currentTimeMillis();

            renderFrame(scene, camera, light);
            mis.newPixels();

            long endTime = System.currentTimeMillis();
            long frameTime = endTime - startTime;

           // System.out.println("Frame time: " + frameTime + "ms");
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static void renderFrame(Scene scene, Camera camera, Light light) {
        for (int y = 0; y < RES_Y; ++y) {
            for (int x = 0; x < RES_X; ++x) {
                String cacheKey = generateCacheKey(camera, x, y);
                Integer cachedColor = Cache.get(cacheKey);
                if (cachedColor != null) {
                    pixels[y * RES_X + x] = cachedColor;
                } else {
                    int color = RayTracingEngine.traceRayWithSuperSampling(scene, light, camera, x, y, useBVH);
                   // int color = RayTracingEngine.traceRayWithEnhancedSuperSampling(scene, light, camera, x, y, useBVH);
                     //int color = traceRayWithoutSuperSampling(scene, light, camera, x, y, useBVH);

                    pixels[y * RES_X + x] = color;
                    Cache.put(cacheKey, color);
                }
            }
        }
    }

    public static int traceRayWithoutSuperSampling(Scene scene, Light light, Camera camera, int x, int y, boolean useBVH) {
        Ray ray = camera.generateRay(x, y);
        return RayTracingEngine.traceRay(scene, ray, light, camera, 1, useBVH);
    }

    private static String generateCacheKey(Camera camera, int x, int y) {
        return camera.getPosition().toString() + camera.getDirection().toString() + "_" + x + "_" + y;
    }

}

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene {
    private Skydome skydome;
    private List<Cylinder> cylinders;
    private List<Light> lights;
    private Camera camera;
    private CopyOnWriteArrayList<Quadric> quadrics;
    private BVH bvh;

    public Scene() {
        this.quadrics = new CopyOnWriteArrayList<>();
        this.lights = new CopyOnWriteArrayList<>();
        this.cylinders = new CopyOnWriteArrayList<>();
    }

    public void buildBVH() {
        this.bvh = new BVH(new ArrayList<>(quadrics));
    }

    public BVH getBVH() {
        return bvh;
    }

    public void addSolid(Quadric solid) {
        this.quadrics.add(solid);
    }

    public void clearSolids() {
        quadrics.clear();
    }

    public Skydome getSkydome() {
        return skydome;
    }

    public void addQuadric(Quadric quadric) {
        if (quadric instanceof Skydome) {
            this.skydome = (Skydome) quadric;
        } else {
            quadrics.add(quadric);
        }
        Cache.invalidateAll();
    }

    public void addCylinder(Cylinder cylinder) {
        cylinders.add(cylinder);
    }

    public List<Quadric> getQuadrics() {
        return quadrics;
    }

    public List<Cylinder> getCylinders() {
        return cylinders;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}

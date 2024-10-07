public class Camera {
    Vector3f position;
    Vector3f direction;
    Vector3f right;
    Vector3f up;
    double fov;
    double aspectRatio;
    private int resX;
    private int resY;

    public Camera(Vector3f pos, Vector3f dir, double fov, int resX, int resY) {
        this.position = pos;
        this.direction = dir.normalize();
        this.fov = fov;
        this.aspectRatio = (double) resX / resY;
        this.resX = resX;
        this.resY = resY;
        this.right = this.direction.cross(new Vector3f(0, 1, 0)).normalize();
        this.up = this.right.cross(this.direction).normalize();
    }

    public int getResX() {
        return resX;
    }

    public void setResX(int resX) {
        this.resX = resX;
    }

    public int getResY() {
        return resY;
    }

    public void setResY(int resY) {
        this.resY = resY;
    }


    public void setPosition(Vector3f position) {
        this.position = position;
    }

    

    public Vector3f getPosition(){
        return position;
    }

    public Vector3f getDirection(){
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }


    public Ray generateRay(double x, double y) {
        double screenX = (2 * (x + 0.5) / resX - 1) * aspectRatio * Math.tan(fov / 2);
        double screenY = (1 - 2 * (y + 0.5) / resY) * Math.tan(fov / 2);
        Vector3f rayDirection = direction.add(right.scale((float) screenX)).add(up.scale((float) screenY)).normalize();
        return new Ray(position, rayDirection);
    }
}

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public abstract class Quadric {
    protected float a, b, c, d, e, f, g, h, i, j;
    protected int color;

    protected String name;
    protected Vector3f center;
    protected Vector3f baseColor;

    public float reflection = 0.2f;
    public float transparency = 0.2f;
    public float refractionIndex = 1.0f;
   // public abstract AABB getBoundingBox();

    public Quadric(float a, float b, float c, float d, float e, float f, float g, float h, float i, float j, int color,String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
        this.color = color;
        this.baseColor = new Vector3f(
                (float) Math.pow(((color >> 16) & 0xFF) / 255.0f, 2.2),
                (float) Math.pow(((color >> 8) & 0xFF) / 255.0f, 2.2),
                (float) Math.pow((color & 0xFF) / 255.0f, 2.2)
        );
        this.center = new Vector3f(-g / (2 * a), -h / (2 * b), -i / (2 * c));
        this.name = name;
    }

    public AABB getBoundingBox() {

        return new AABB(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
    }

    public String getName() {
        return name;
    }

    public void setReflection(float reflection) {
        this.reflection = reflection;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    public void setRefractionIndex(float refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float intersects(Ray ray) {
        Vector3f p = ray.origin;
        Vector3f v = ray.direction;

        float px = p.x, py = p.y, pz = p.z;
        float vx = v.x, vy = v.y, vz = v.z;

        float A = a * vx * vx + b * vy * vy + c * vz * vz +
                2 * (d * vx * vy + e * vx * vz + f * vy * vz);
        float B = 2 * (a * px * vx + b * py * vy + c * pz * vz +
                d * (px * vy + py * vx) +
                e * (px * vz + pz * vx) +
                f * (py * vz + pz * vy)) +
                g * vx + h * vy + i * vz;
        float C = a * px * px + b * py * py + c * pz * pz +
                2 * (d * px * py + e * px * pz + f * py * pz) +
                g * px + h * py + i * pz + j;

        float discriminant = B * B - 4 * A * C;
        if (discriminant < 0) {
            return -1.0f;
        }

        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-B - sqrtDiscriminant) / (2.0f * A);
        float t2 = (-B + sqrtDiscriminant) / (2.0f * A);

        if (t1 > 0 && t2 > 0) {
            return Math.min(t1, t2);
        } else if (t1 > 0) {
            return t1;
        } else if (t2 > 0) {
            return t2;
        } else {
            return -1.0f;
        }
    }

    public Vector3f getNormal(Vector3f point) {
        float nx = 2 * a * point.x + 2 * d * point.y + 2 * e * point.z + g;
        float ny = 2 * b * point.y + 2 * d * point.x + 2 * f * point.z + h;
        float nz = 2 * c * point.z + 2 * e * point.x + 2 * f * point.y + i;
        Vector3f normal = new Vector3f(nx, ny, nz);
        return normal.normalize();
    }

    public int getShadows(Vector3f intersectionPoint, Light light, Scene scene, int numShadowSamples) {
        float shadow = 0.0f;

        for (int i = 0; i < numShadowSamples; i++) {
            Vector3f lightSample = light.randomPoint();
            Vector3f lightDirection = lightSample.subtract(intersectionPoint).normalize();
            Ray shadowRay = new Ray(intersectionPoint.add(lightDirection.scale(0.005f)), lightDirection);

            boolean isOccluded = false;
            for (Quadric q : scene.getQuadrics()) {
                if (q != this) {
                    float t = q.intersects(shadowRay);
                    if (t > 0) {
                        isOccluded = true;
                        break;
                    }
                }
            }

            if (!isOccluded) {
                shadow += 1.0f;
            }
        }

        shadow /= numShadowSamples;


        return (int) ((1.0f - shadow) * numShadowSamples);
    }


    public int getShadedColor(Vector3f intersectionPoint, Vector3f normal, Light light, Camera camera, int shadowLevel, int numShadowSamples) {
        Vector3f baseColorVector = baseColor;

        if (this instanceof Plane) {
            int checkerColor = ((Plane) this).getColor(intersectionPoint);
            baseColorVector = Vector3f.intToVector3f(checkerColor);
        }

        Vector3f lightColor = light.intensity;
        Vector3f viewDirection = camera.position.subtract(intersectionPoint).normalize();
        Vector3f lightDirection = light.position.subtract(intersectionPoint).normalize();
        Vector3f H = lightDirection.add(viewDirection).normalize();

        float F0 = 0.04f;
        float F = F0 + (1 - F0) * (float) Math.pow(1.0 - Math.max(0.0, normal.dot(viewDirection)), 5);

        float NdotL = Math.max(0.0f, normal.dot(lightDirection));
        float NdotV = Math.max(0.0f, normal.dot(viewDirection));
        float NdotH = Math.max(0.0f, normal.dot(H));
        float VdotH = Math.max(0.0f, viewDirection.dot(H));

        float k = 0.5f;
        float G1L = 2.0f * NdotH * NdotV / VdotH;
        float G1V = 2.0f * NdotH * NdotL / VdotH;
        float G = Math.min(1.0f, Math.min(G1L, G1V));

        float alpha = 0.1f;
        float denom = (NdotH * NdotH) * (alpha * alpha - 1.0f) + 1.0f;
        float D = (float) (alpha * alpha / (Math.PI * denom * denom));

        float specular = (F * G * D) / (4.0f * NdotL * NdotV);
        float diffuse = 0.5f * NdotL / (float) Math.PI;

        float shadow = 1.0f - (float) shadowLevel / numShadowSamples;
        diffuse *= shadow;
        specular *= shadow;

        Vector3f color = baseColorVector.multiply(lightColor.multiply(diffuse + 0.5f * specular));

        color.x = (float) Math.pow(Math.min(1.0f, color.x), 1.0 / 2.2);
        color.y = (float) Math.pow(Math.min(1.0f, color.y), 1.0 / 2.2);
        color.z = (float) Math.pow(Math.min(1.0f, color.z), 1.0 / 2.2);

        int red = Math.min(255, (int) (color.x * 255));
        int green = Math.min(255, (int) (color.y * 255));
        int blue = Math.min(255, (int) (color.z * 255));

        return (red << 16) | (green << 8) | blue;
    }





    public boolean contains(Vector3f point) {
        float px = point.x, py = point.y, pz = point.z;
        float value = a * px * px + b * py * py + c * pz * pz +
                2 * (d * px * py + e * px * pz + f * py * pz) +
                g * px + h * py + i * pz + j;
        return value <= 0;
    }






    public void translate(Vector3f translation) {
        RealMatrix T = MatrixUtils.createRealMatrix(new double[][] {
                {1, 0, 0, translation.x},
                {0, 1, 0, translation.y},
                {0, 0, 1, translation.z},
                {0, 0, 0, 1}
        });

        RealMatrix T_inv = inverse(T);
        RealMatrix T_inv_T = transpose(T_inv);

        RealMatrix Q = MatrixUtils.createRealMatrix(new double[][]{
                {a, d, e, g},
                {d, b, f, h},
                {e, f, c, i},
                {g, h, i, j}
        });

        RealMatrix Q_prime = T_inv_T.multiply(Q).multiply(T_inv);

        double[][] q = Q_prime.getData();
        a = (float) q[0][0];
        b = (float) q[1][1];
        c = (float) q[2][2];
        d = (float) q[0][1];
        e = (float) q[0][2];
        f = (float) q[1][2];
        g = (float) q[0][3];
        h = (float) q[1][3];
        i = (float) q[2][3];
        j = (float) q[3][3];

        this.center = this.center.add(translation);
    }

    public void scale(Vector3f scaleFactors) {
        RealMatrix S = MatrixUtils.createRealMatrix(new double[][]{
                {scaleFactors.x, 0, 0, 0},
                {0, scaleFactors.y, 0, 0},
                {0, 0, scaleFactors.z, 0},
                {0, 0, 0, 1}
        });

        RealMatrix Q = MatrixUtils.createRealMatrix(new double[][]{
                {a, d, e, g},
                {d, b, f, h},
                {e, f, c, i},
                {g, h, i, j}
        });

        RealMatrix S_inv = inverse(S);
        RealMatrix S_inv_T = transpose(S_inv);

        RealMatrix Q_prime = S_inv_T.multiply(Q).multiply(S_inv);

        double[][] q = Q_prime.getData();
        a = (float) q[0][0];
        b = (float) q[1][1];
        c = (float) q[2][2];
        d = (float) q[0][1];
        e = (float) q[0][2];
        f = (float) q[1][2];
        g = (float) q[0][3];
        h = (float) q[1][3];
        i = (float) q[2][3];
        j = (float) q[3][3];
    }

    public void rotateX(double angle) {
        RealMatrix Rx = MatrixUtils.createRealMatrix(new double[][] {
                {1, 0, 0, 0},
                {0, Math.cos(angle), -Math.sin(angle), 0},
                {0, Math.sin(angle), Math.cos(angle), 0},
                {0, 0, 0, 1}
        });

        applyRotation(Rx);
    }

    public void rotateY(double angle) {
        RealMatrix Ry = MatrixUtils.createRealMatrix(new double[][] {
                {Math.cos(angle), 0, Math.sin(angle), 0},
                {0, 1, 0, 0},
                {-Math.sin(angle), 0, Math.cos(angle), 0},
                {0, 0, 0, 1}
        });

        applyRotation(Ry);
    }

    public void rotateZ(double angle) {
        RealMatrix Rz = MatrixUtils.createRealMatrix(new double[][] {
                {Math.cos(angle), -Math.sin(angle), 0, 0},
                {Math.sin(angle), Math.cos(angle), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });

        applyRotation(Rz);
    }
    public float getRadius() {

        float radiusSquared = center.lengthSquared() - (j / a);
        return (float) Math.sqrt(radiusSquared);
    }


    public void translate2(Vector3f translation) {
        g -= 2 * a * translation.x + d * translation.y + e * translation.z;
        h -= 2 * b * translation.y + d * translation.x + f * translation.z;
        i -= 2 * c * translation.z + e * translation.x + f * translation.y;
        j -= a * translation.x * translation.x + b * translation.y * translation.y + c * translation.z * translation.z
                + d * translation.x * translation.y + e * translation.x * translation.z + f * translation.y * translation.z
                + g * translation.x + h * translation.y + i * translation.z;
        center = center.add(translation);


    }



    private void applyRotation(RealMatrix rotationMatrix) {
        RealMatrix rotationMatrixInverse = inverse(rotationMatrix);
        RealMatrix rotationMatrixInverseTranspose = transpose(rotationMatrixInverse);

        RealMatrix Q = MatrixUtils.createRealMatrix(new double[][] {
                {a, d, e, g},
                {d, b, f, h},
                {e, f, c, i},
                {g, h, i, j}
        });

        RealMatrix Q_prime = rotationMatrixInverseTranspose.multiply(Q).multiply(rotationMatrixInverse);

        double[][] q = Q_prime.getData();
        a = (float) q[0][0];
        b = (float) q[1][1];
        c = (float) q[2][2];
        d = (float) q[0][1];
        e = (float) q[0][2];
        f = (float) q[1][2];
        g = (float) q[0][3];
        h = (float) q[1][3];
        i = (float) q[2][3];
        j = (float) q[3][3];
    }

    private RealMatrix inverse(RealMatrix matrix) {
        return new LUDecomposition(matrix).getSolver().getInverse();
    }

    private RealMatrix transpose(RealMatrix matrix) {
        return matrix.transpose();
    }
}

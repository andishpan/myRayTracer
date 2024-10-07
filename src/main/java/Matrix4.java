import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;

public class Matrix4 {

    float[][] daten = new float[4][4];

    public Matrix4() {
        // Initialize with the identity matrix
        daten = new float[][] {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
    }

    public Matrix4(float[][] data) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.daten[i][j] = data[i][j];
            }
        }
    }

    public Matrix4(Matrix4 copy) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.daten[i][j] = copy.daten[i][j];
            }
        }
    }

    public Matrix4 translate(float x, float y, float z) {
        Matrix4 translation = new Matrix4();
        translation.daten[0][3] = x;
        translation.daten[1][3] = y;
        translation.daten[2][3] = z;
        return this.multiply(translation);
    }

    public Matrix4 scale(float uniformFactor) {
        Matrix4 scaling = new Matrix4();
        scaling.daten[0][0] = uniformFactor;
        scaling.daten[1][1] = uniformFactor;
        scaling.daten[2][2] = uniformFactor;
        return this.multiply(scaling);
    }

    public Matrix4 scale(float sx, float sy, float sz) {
        Matrix4 scaling = new Matrix4();
        scaling.daten[0][0] = sx;
        scaling.daten[1][1] = sy;
        scaling.daten[2][2] = sz;
        return this.multiply(scaling);
    }

    public Matrix4 rotateX(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.daten[1][1] = (float) Math.cos(angle);
        rotation.daten[1][2] = -(float) Math.sin(angle);
        rotation.daten[2][1] = (float) Math.sin(angle);
        rotation.daten[2][2] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public Matrix4 rotateY(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.daten[0][0] = (float) Math.cos(angle);
        rotation.daten[0][2] = (float) Math.sin(angle);
        rotation.daten[2][0] = -(float) Math.sin(angle);
        rotation.daten[2][2] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public Matrix4 rotateZ(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.daten[0][0] = (float) Math.cos(angle);
        rotation.daten[0][1] = -(float) Math.sin(angle);
        rotation.daten[1][0] = (float) Math.sin(angle);
        rotation.daten[1][1] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public Matrix4 multiply(Matrix4 other) {
        float[][] result = new float[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                for (int k = 0; k < 4; k++) {
                    result[row][col] += this.daten[row][k] * other.daten[k][col];
                }
            }
        }
        this.daten = result;
        return this;
    }

    public Matrix4 transpose() {
        Matrix4 result = new Matrix4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.daten[i][j] = this.daten[j][i];
            }
        }
        return result;
    }

    public Matrix4 inverse() {
        double[][] matrixData = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrixData[i][j] = this.daten[i][j];
            }
        }
        RealMatrix matrix = MatrixUtils.createRealMatrix(matrixData);
        RealMatrix inverseMatrix;
        try {
            inverseMatrix = new LUDecomposition(matrix).getSolver().getInverse();
        } catch (Exception e) {
            throw new RuntimeException("Matrix inversion failed", e);
        }
        double[][] resultData = inverseMatrix.getData();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.daten[i][j] = (float) resultData[i][j];
            }
        }
        return this;
    }

    public Vector3f transform(Vector3f v) {
        float x = daten[0][0] * v.x + daten[0][1] * v.y + daten[0][2] * v.z + daten[0][3];
        float y = daten[1][0] * v.x + daten[1][1] * v.y + daten[1][2] * v.z + daten[1][3];
        float z = daten[2][0] * v.x + daten[2][1] * v.y + daten[2][2] * v.z + daten[2][3];
        return new Vector3f(x, y, z);
    }
}

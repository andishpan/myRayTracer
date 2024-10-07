import java.util.ArrayList;
import java.util.List;

public class BVHNode {
    public AABB boundingBox;
    public BVHNode left;
    public BVHNode right;
    public List<Quadric> objects;

    public String name;

    public BVHNode() {
        this.boundingBox = new AABB();
        this.left = null;
        this.right = null;
        this.objects = new ArrayList<>();
    }



    public boolean isLeaf() {
        return left == null && right == null;
    }
}

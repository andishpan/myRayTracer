import java.util.Comparator;
import java.util.List;

public class BVH {
    private BVHNode root;

    public BVH(List<Quadric> objects) {
        System.out.println("Building BVH with " + objects.size() + " objects");
        this.root = buildBVH(objects);
        System.out.println("BVH built successfully");
    }

    private BVHNode buildBVH(List<Quadric> objects) {
        if (objects.size() == 0) return null;

        BVHNode node = new BVHNode();
        if (objects.size() == 1) {
            node.objects = objects;
            node.boundingBox = objects.get(0).getBoundingBox();
            System.out.println("Leaf node with 1 object: " + node.objects.get(0).getName());
        } else {
            //  bounding box for  node
            AABB bbox = new AABB();
            for (Quadric obj : objects) {
                bbox = bbox.union(obj.getBoundingBox());
            }
            node.boundingBox = bbox;

            // split objects into two groups
            int axis = bbox.getLongestAxis();
            objects.sort(Comparator.comparingDouble(o -> o.getBoundingBox().getCenter().get(axis)));

            int mid = objects.size() / 2;
            List<Quadric> leftObjects = objects.subList(0, mid);
            List<Quadric> rightObjects = objects.subList(mid, objects.size());

            node.left = buildBVH(leftObjects);
            node.right = buildBVH(rightObjects);

            // Ensure both subtrees are not null
            if (node.left == null) {
                node.left = new BVHNode();
                node.left.objects = leftObjects;
                node.left.boundingBox = leftObjects.get(0).getBoundingBox();
            }

            if (node.right == null) {
                node.right = new BVHNode();
                node.right.objects = rightObjects;
                node.right.boundingBox = rightObjects.get(0).getBoundingBox();
            }

            // if no left or right => leaf
            if (node.left == null && node.right == null) {
                node.objects = objects;
            }
        }

        // Debug statement
        System.out.println("Node created with bounding box: " + node.boundingBox +
                " | Left: " + (node.left != null ? "Yes" : "No") +
                " | Right: " + (node.right != null ? "Yes" : "No") +
                " | Objects: " + (node.objects != null ? node.objects.size() : 0));

        return node;
    }


    public BVHNode getRoot() {
        return root;
    }
}

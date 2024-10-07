import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {
    private BufferedImage image;

    public Texture(String filename) throws IOException {
        image = ImageIO.read(new File(filename));
    }

    public int getColor(float u, float v) {
        u = clamp(u, 0.0f, 1.0f);
        v = clamp(v, 0.0f, 1.0f);
        int x = (int) (u * (image.getWidth() - 1));
        // invert v coordinate
        int y = (int) ((1 - v) * (image.getHeight() - 1));
        return image.getRGB(x, y);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
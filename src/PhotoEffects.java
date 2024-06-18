import java.awt.Color;
import java.awt.image.BufferedImage;

public class PhotoEffects {

    public static BufferedImage grayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage invertColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = 255 - ((p >> 16) & 0xff);
                int g = 255 - ((p >> 8) & 0xff);
                int b = 255 - (p & 0xff);

                p = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage binarize(BufferedImage image, int threshold) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;
                int newPixel = (avg > threshold) ? 0xFFFFFFFF : 0xFF000000;
                p = (a << 24) | newPixel;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage posterize(BufferedImage image, int levels) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                r = (int) ((255.0 / (levels - 1)) * Math.round((levels - 1) * r / 255.0));
                g = (int) ((255.0 / (levels - 1)) * Math.round((levels - 1) * g / 255.0));
                b = (int) ((255.0 / (levels - 1)) * Math.round((levels - 1) * b / 255.0));

                p = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage brightness(BufferedImage image, int brightness) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = Math.max(0, Math.min(255, ((p >> 16) & 0xff) + brightness));
                int g = Math.max(0, Math.min(255, ((p >> 8) & 0xff) + brightness));
                int b = Math.max(0, Math.min(255, (p & 0xff) + brightness));

                p = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage sepia(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int depth = 20;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                tr = Math.min(255, tr + depth);
                tg = Math.min(255, tg + depth);
                tb = Math.min(255, tb + depth);

                p = (a << 24) | (tr << 16) | (tg << 8) | tb;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

}


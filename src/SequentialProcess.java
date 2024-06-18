import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class SequentialProcess {
    protected String[][] files;
    protected int option;
    protected String[][] filteredFiles;


    public SequentialProcess(String[][] files, int option) {
        this.files = files;
        this.option = option;
        this.filteredFiles = new String[files.length][2];
    }

    public String[][] applyFilter() {
        for (String[] file : files) {
            byte[] data = Base64.getDecoder().decode(file[1]);
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
                switch (option) {
                    case 1:
                        PhotoEffects.grayscale(image);
                        break;
                    case 2:
                        PhotoEffects.invertColors(image);
                        break;
                    case 3:
                        PhotoEffects.binarize(image, 128);
                        break;
                    case 4:
                        PhotoEffects.posterize(image, 8);
                        break;
                    case 5:
                        PhotoEffects.brightness(image, 50);
                        break;
                    case 6:
                        PhotoEffects.sepia(image);
                        break;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                filteredFiles[0][0] = file[0];
                filteredFiles[0][1] = base64;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filteredFiles;
    }
}

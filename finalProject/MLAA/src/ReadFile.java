import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ReadFile {

    public static void main(String[] args) throws Exception {

        // Image formats with default lossless compression highly recommended
        File file = new File("/Users/mjg29296/Documents/COS320/COS320 Assignments/finalProject/testImages/metalGearSolid.png");
        BufferedImage image = ImageIO.read(file);

        AlgorithmMLAA mlaa = new AlgorithmMLAA();
        image = mlaa.algorithm(image);

        File newFile = new File("/Users/mjg29296/Documents/COS320/COS320 Assignments/finalProject/testImages/metalGearSolidMLAA.png");
        ImageIO.write(image, "png", newFile);
        System.out.println("Image rendering complete: check for new image.");
    }
}

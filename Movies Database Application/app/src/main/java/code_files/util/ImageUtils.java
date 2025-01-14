package code_files.util;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ImageUtils {

    /**
     * Creates a scaled ImageIcon from the given image path.
     * 
     * @param imagePath The URL or URI string of the image.
     * @param width The desired width of the scaled image.
     * @param height The desired height of the scaled image.
     * @return An ImageIcon object containing the scaled image.
     */
    public ImageIcon createScaledImageIcon(String imagePath, int width, int height) {
        try {
            // Convert the path to URI and then to URL
            URI posterUri = new URI(imagePath);
            URL posterUrl = posterUri.toURL();

            // Create an ImageIcon from the URL
            ImageIcon posterIcon = new ImageIcon(posterUrl);

            // Scale the image
            Image img = posterIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);

        } catch (MalformedURLException | java.net.URISyntaxException e) {
            // Catch invalid URL or URI exceptions
            System.err.println("Invalid poster URL or URI: " + imagePath);
            e.printStackTrace();
            return null;  // Return null or a default image in case of an error
        }
    }
}

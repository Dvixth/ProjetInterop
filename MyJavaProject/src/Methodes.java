import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.imageio.ImageIO;
import org.h2.tools.SimpleResultSet;


public class Methodes {

// Fonction pour générer les valeurs de la fonction gaussienne pour un point (x, y)
    // Utilisation de la formule f(x, y) = k * exp(-(x^2 + y^2) / (2 * sigma^2))
    // où k est une constante de normalisation et sigma est l'écart-type
    // fonction vus au TP graphque de la premier semester 

    static double genererValeurs(int x, int y, double sigma) {
        return Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
    }
        // Fonction pour générer une table de valeurs gaussiennes pour une grille de taille donnée
    public static ResultSet getGaussianTable(int size) throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("x", Types.INTEGER, 0, 0);
        rs.addColumn("y", Types.INTEGER, 0, 0);
        rs.addColumn("valeur", Types.DOUBLE, 0, 0);

        int halfSize = size / 2;
        double sigma = 1.0; // écart-type
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                double value = Methodes.genererValeurs(x, y, sigma);
            // Ajouter les valeurs à la table de résultats
                rs.addRow(x, y, value);
            }
        }
        return rs;
    }
    
    public static ResultSet getRGBImage(){

        String path ="MyJavaProject/hand.jpg";
       
        SimpleResultSet rs = new SimpleResultSet();
        
        try {
            BufferedImage image = ImageIO.read(new File(path)); 
            int width = image.getWidth();
            int height = image.getHeight();
            
            rs.addColumn("x", java.sql.Types.INTEGER, 0, 0);
            rs.addColumn("y", java.sql.Types.INTEGER, 0, 0);
            rs.addColumn("r", java.sql.Types.INTEGER, 0, 0);
            rs.addColumn("g", java.sql.Types.INTEGER, 0, 0);
            rs.addColumn("b", java.sql.Types.INTEGER, 0, 0);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    rs.addRow(x, y, red, green, blue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static void exo6gaussien(String scriptPath, String imagePath, int filterSize, String outputDirectory) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("python3", scriptPath, imagePath, Integer.toString(filterSize), outputDirectory);
        Process process = builder.start();
        process.waitFor();
        System.out.println("Script Python exécuté avec succès !");
    }
    

    public static void exo6marrhildreth(String scriptName, String imagePath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("python3", scriptName, imagePath);
        Process process = builder.start();
        process.waitFor();
        System.out.println("Script Python exécuté avec succès !");
    }

    

}
    

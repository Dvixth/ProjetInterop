import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.imageio.ImageIO;

import org.h2.tools.SimpleResultSet;

public class Methodes {

    //Méthode qui génère des valeurs 
    static double genererValeurs(int x, int y) {
        double sigma = 1.0; // écart-type
        //double mean = 0.0; // moyenne
        return (1.0 / (2 * Math.PI * sigma * sigma)) * Math.exp(-((x * x + y * y) / (2 * sigma * sigma)));
    }

    public static ResultSet getGaussianTable(Connection conn, Integer size) throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("x", Types.INTEGER, 0, 0);
        rs.addColumn("y", Types.INTEGER, 0, 0);
        rs.addColumn("valeur", Types.DOUBLE, 0, 0);

        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                double value = Methodes.genererValeurs(x, y);
                rs.addRow(x, y, value);
            }
        }
        return rs;
    }

    public static ResultSet getRGBImage(){

        String path ="ProjetInterop/MyJavaProject/hand.jpg";
       
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

}
    

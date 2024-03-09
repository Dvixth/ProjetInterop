import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.h2.tools.SimpleResultSet;

public class Methode {

    //Méthode qui génère des valeurs 
    static double genererValeurs(int x, int y) {
        double sigma = 1.0; // écart-type
        //double mean = 0.0; // moyenne
        return (1.0 / (2 * Math.PI * sigma * sigma)) * Math.exp(-((x * x + y * y) / (2 * sigma * sigma)));
    }

    // Méthode getGaussianTable doit être déclarée comme public static
    public static ResultSet getGaussianTable(Connection conn, Integer size) throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("x", Types.INTEGER, 0, 0);
        rs.addColumn("y", Types.INTEGER, 0, 0);
        rs.addColumn("valeur", Types.DOUBLE, 0, 0);

        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                double value = Methode.genererValeurs(x, y);
                rs.addRow(x, y, value);
            }
        }
        return rs;
    }

    
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.h2.tools.SimpleResultSet;

public class Main {
    // Méthode getGaussianTable doit être déclarée comme public static
    public static ResultSet getGaussianTable(Connection conn, Integer size) throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("x", Types.INTEGER, 0, 0);
        rs.addColumn("y", Types.INTEGER, 0, 0);
        rs.addColumn("valeur", Types.DOUBLE, 0, 0);

        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                double value = gaussianFunction(x, y);
                rs.addRow(x, y, value);
            }
        }
        return rs;
    }

    // Fonction gaussienne simple pour générer des valeurs
    private static double gaussianFunction(int x, int y) {
        double sigma = 1.0; // écart-type
        double mean = 0.0; // moyenne
        return (1.0 / (2 * Math.PI * sigma * sigma)) * Math.exp(-((x * x + y * y) / (2 * sigma * sigma)));
    }

    public static void main(String[] args) {
        String connUrl = "jdbc:h2:./h2database"; // URL de connexion à la base de données H2
        String username = "sa"; // Nom d'utilisateur
        String password = ""; // Mot de passe (vide dans cet exemple)

        // Bloc try-with-resources pour gérer automatiquement la fermeture de la connexion
        try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
            // Création de l'alias de fonction GAUSSIENNE
            String createAliasSql = "CREATE ALIAS IF NOT EXISTS GAUSSIENNE FOR \"Main.getGaussianTable\"";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createAliasSql);
            }

            // Création manuelle de la table GAUSSIENNE
            String createTableSql = "CREATE TABLE GAUSSIENNE (x INT, y INT, valeur DOUBLE)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSql);
            }

            // Utilisation de l'alias de fonction pour générer les données de la table virtuelle GAUSSIENNE
            String insertDataSql = "INSERT INTO GAUSSIENNE (x, y, valeur) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertDataSql)) {
                ResultSet rs = getGaussianTable(conn, 5); // Taille k = 5
                while (rs.next()) {
                    pstmt.setInt(1, rs.getInt("x"));
                    pstmt.setInt(2, rs.getInt("y"));
                    pstmt.setDouble(3, rs.getDouble("valeur"));
                    pstmt.executeUpdate();
                }
            }

            // Affichage des valeurs de la table GAUSSIENNE dans la console
            String selectSql = "SELECT * FROM GAUSSIENNE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    double value = rs.getDouble("valeur");
                    System.out.println("x: " + x + ", y: " + y + ", valeur: " + value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

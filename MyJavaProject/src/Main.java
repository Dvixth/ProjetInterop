import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.h2.tools.SimpleResultSet;

public class Main {

    public static void exercice1(){

        String connUrl = "jdbc:h2:./h2database"; // URL de connexion à la base de données H2
        String username = "sa"; // Nom d'utilisateur
        String password = ""; // Mot de passe

        try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
            // Création de l'alias de fonction GAUSSIENNE
            String creeAlias = "CREATE ALIAS IF NOT EXISTS GAUSSIENNE FOR \"Methode.getGaussianTable\"";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(creeAlias);
            }

            // Création manuelle de la table GAUSSIENNE
            String creeTable = "CREATE TABLE GAUSSIENNE (x INT, y INT, valeur DOUBLE)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(creeTable);
            }

            // Utilisation de l'alias de fonction pour générer les données de la table virtuelle GAUSSIENNE
            String insererDonnes = "INSERT INTO GAUSSIENNE (x, y, valeur) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insererDonnes)) {
                ResultSet rs = Methode.getGaussianTable(conn, 5); // Taille k = 5
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

    public static void exercice2(){

    }



   
    public static void main(String[] args) {

        exercice1();

       
    }
}

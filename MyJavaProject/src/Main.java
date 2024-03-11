import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        String connexionUrl = "jdbc:h2:mem:database"; // URL de connexion à la base de données H2 en mémoire
        String username = "sa"; // Nom d'utilisateur
        String password = ""; // Mot de passe

        try (Connection connexion = DriverManager.getConnection(connexionUrl, username, password)) {
            exercice1(connexion);
            exercice2(connexion);
            exercice3(connexion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exercice1(Connection connexion) {
        try {
            String creeAlias = "CREATE ALIAS IF NOT EXISTS GAUSSIENNE FOR \"Methode.getGaussianTable\"";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeAlias);
            }
            String creeTable = "CREATE TABLE GAUSSIENNE (x INT, y INT, valeur DOUBLE)";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeTable);
            }

            String insererDonnes = "INSERT INTO GAUSSIENNE (x, y, valeur) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connexion.prepareStatement(insererDonnes)) {
                ResultSet rs = Methode.getGaussianTable(connexion, 5); // Taille k = 5
                while (rs.next()) {
                    pstmt.setInt(1, rs.getInt("x"));
                    pstmt.setInt(2, rs.getInt("y"));
                    pstmt.setDouble(3, rs.getDouble("valeur"));
                    pstmt.executeUpdate();
                }
            }

            String selectSql = "SELECT * FROM GAUSSIENNE";
            try (Statement stmt = connexion.createStatement();
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

    public static void exercice2(Connection connexion) {
        try {
            String creeAlias = "CREATE ALIAS IF NOT EXISTS RGBIMAGE FOR \"Methode.getRGBImage\"";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeAlias);
            }

            String creeTable = "CREATE TABLE RGBIMAGE (x INT, y INT, r INT, g INT, b INT)";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeTable);
            }

            String insererDonnes = "INSERT INTO RGBIMAGE (x, y, r, g, b) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connexion.prepareStatement(insererDonnes)) {
                ResultSet rs = Methode.getRGBImage();
                while (rs.next()) {
                    pstmt.setInt(1, rs.getInt("x"));
                    pstmt.setInt(2, rs.getInt("y"));
                    pstmt.setInt(3, rs.getInt("r"));
                    pstmt.setInt(4, rs.getInt("g"));
                    pstmt.setInt(5, rs.getInt("b"));
                    pstmt.executeUpdate();
                }
            }

            String selectSql = "SELECT * FROM RGBIMAGE";
            try (Statement stmt = connexion.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int r = rs.getInt("r");
                    int g = rs.getInt("g");
                    int b = rs.getInt("b");
                    System.out.println("x: " + x + ", y: " + y + ", r :" + r + ", g: " + g + ", b: " + b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exercice3(Connection connexion) {
        try {
            String createTableQuery = "CREATE TABLE UNEIMAGE AS SELECT * FROM RGBIMAGE";
            try (Statement statement = connexion.createStatement()) {
                statement.executeUpdate(createTableQuery);
            }

            String selectSql = "SELECT * FROM UNEIMAGE";
            try (Statement stmt = connexion.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int r = rs.getInt("r");
                    int g = rs.getInt("g");
                    int b = rs.getInt("b");
                    System.out.println("x: " + x + ", y: " + y + ", r :" + r + ", g: " + g + ", b: " + b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

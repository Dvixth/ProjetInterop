import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        String connexionUrl = "jdbc:h2:mem:database"; // URL de connexion à la base de données H2 en mémoire
        String username = "sa"; // Nom d'utilisateur
        String password = ""; // Mot de passe

        try (Connection connexion = DriverManager.getConnection(connexionUrl, username, password)) {

            System.out.println("Pour k=5 : ");
            gaussien(connexion,5);
            System.out.println("Pour k=7 : ");
            gaussien(connexion,7);
            saveRGBImageToSqlTable(connexion);
            niveauDeGris(connexion);
            seuillage(connexion);
            // Appeler la fonction pour le filtre gaussien de taille 5 et 7 
            Methodes.exo6gaussien("./MyPythonProject/gaussian.py", "./MyJavaProject/building.png", 5, "./ResultatsImage");
            Methodes.exo6gaussien("./MyPythonProject/gaussian.py", "./MyJavaProject/building.png", 7, "./ResultatsImage");
            //Appeler le script Python pour Marr-Hildreth
            Methodes.exo6marrhildreth("./MyPythonProject/marr_hildreth_python.py", "./MyJavaProject/building.png");
            downscaling(connexion, "./ResultatsImage/seuillagehysterisis.png", "./ResultatsImage/antialiassage.png", 4);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void gaussien(Connection connexion, int k) {
        try {
            // Création de l'alias pour la table virtuelle GAUSSIENNE
            String createAlias = "CREATE ALIAS IF NOT EXISTS GAUSSIENNE FOR \"Methodes.getGaussianTable\"";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(createAlias);
            }
 
            // Vérification si la table GAUSSIENNE existe déjà
            boolean tableExists = false;
            try (ResultSet rs = connexion.getMetaData().getTables(null, null, "GAUSSIENNE", null)) {
                if (rs.next()) {
                    tableExists = true;
                }
            }
 
            // Création de la table GAUSSIENNE si elle n'existe pas déjà
            if (!tableExists) {
                String createTable = "CREATE TABLE GAUSSIENNE (x INT, y INT, valeur DOUBLE)";
                try (Statement stmt = connexion.createStatement()) {
                    stmt.execute(createTable);
                }
            }
 
            // Insertion des données dans la table GAUSSIENNE pour la taille k spécifiée
            String insertData = "INSERT INTO GAUSSIENNE (x, y, valeur) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connexion.prepareStatement(insertData)) {
                ResultSet rs = Methodes.getGaussianTable(k);
                while (rs.next()) {
                    pstmt.setInt(1, rs.getInt("x"));
                    pstmt.setInt(2, rs.getInt("y"));
                    pstmt.setDouble(3, rs.getDouble("valeur"));
                    pstmt.executeUpdate();
                }
            }
 
            // Sélection et affichage des données de la table GAUSSIENNE
            String selectSql = "SELECT * FROM GAUSSIENNE";
            try (Statement stmt = connexion.createStatement(); ResultSet rs = stmt.executeQuery(selectSql)) {
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

    public static void saveRGBImageToSqlTable(Connection connexion) {
        try {
            String creeAlias = "CREATE ALIAS IF NOT EXISTS RGBIMAGE FOR \"Methodes.getRGBImage\"";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeAlias);
            }

            String creeTable = "CREATE TABLE RGBIMAGE (x INT, y INT, r INT, g INT, b INT)";
            try (Statement stmt = connexion.createStatement()) {
                stmt.execute(creeTable);
            }

            String insererDonnes = "INSERT INTO RGBIMAGE (x, y, r, g, b) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connexion.prepareStatement(insererDonnes)) {
                ResultSet rs = Methodes.getRGBImage();
                while (rs.next()) {
                    pstmt.setInt(1, rs.getInt("x"));
                    pstmt.setInt(2, rs.getInt("y"));
                    pstmt.setInt(3, rs.getInt("r"));
                    pstmt.setInt(4, rs.getInt("g"));
                    pstmt.setInt(5, rs.getInt("b"));
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void niveauDeGris(Connection connexion) throws SQLException {
        try {
            String creeTable = "CREATE TABLE UNEIMAGE AS SELECT * FROM RGBIMAGE";
            try (Statement statement = connexion.createStatement()) {
                statement.execute(creeTable);
            

             /*Effectuer la conversion en niveaux de gris ici on a retirer la division par 3 pour avoir un meilleur résultat ce qui va nous permettre aussi
                ensuite d'appliquer un seuillage sur l'image et avoir un résultat satisfaisant car la division par 3 transforme l'image en niveau de gris foncée 
                ce qui fais que les composantes rgb d'un pixels sont toutes <=128 et ce qui fais que lorsque on applique un seuillage simple qui divise l'image en 
                par un seuille bas <128 et un seuille haut >=128
              */
            statement.executeUpdate("UPDATE UNEIMAGE SET " +
            "r = CAST(((r * 0.3 + g * 0.59 + b * 0.11) ) AS INT), " +
            "g = CAST(((r * 0.3 + g * 0.59 + b * 0.11) ) AS INT), " +
            "b = CAST(((r * 0.3 + g * 0.59 + b * 0.11) ) AS INT);");

               // Récupérer les données de la table UNEIMAGE
               ResultSet resultSet = statement.executeQuery("SELECT x, y, r, g, b FROM UNEIMAGE");

               // Taille de l'image
               int width = 500;
               int height = 391;

               // Listes pour stocker les données
               int[] x = new int[width * height];
               int[] y = new int[width * height];
               int[] r = new int[width * height];
               int[] g = new int[width * height];
               int[] b = new int[width * height];

               int index = 0;
               while (resultSet.next()) {
                   x[index] = resultSet.getInt("x");
                   y[index] = resultSet.getInt("y");
                   r[index] = resultSet.getInt("r");
                   g[index] = resultSet.getInt("g");
                   b[index] = resultSet.getInt("b");
                   index++;
               }
               String filePath="./ResultatsImage/NiveauDeGris.png";
               JNIImageHandler.sauveImage(x, y, r, g, b, width, height,filePath);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }     

      public static void seuillage(Connection connexion) throws SQLException {
        // Effectuer le seuillage simple ou par hystérésis
        System.out.println("Choisissez le type de seuillage à appliquer :");
        System.out.println("1. Seuillage simple");
        System.out.println("2. Seuillage par hystérésis");
        System.out.print("Votre choix : ");

        try (// Récupération du choix de l'utilisateur
        Scanner scanner = new Scanner(System.in)) {
            int choix = scanner.nextInt();

            switch (choix) {
                case 1:
                    seuillageSimple(connexion);
                    break;
                case 2:
                    seuillageHysteresis(connexion);
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    public static void seuillageSimple(Connection connexion) throws SQLException {
        try {
            // Effectuer le seuillage simple sur la table UNEIMAGE
            try (Statement statement = connexion.createStatement()) {
                statement.executeUpdate("UPDATE UNEIMAGE SET " +
                        "r = CASE WHEN r >= 140 THEN 255 ELSE 0 END, " +
                        "g = CASE WHEN g >= 140 THEN 255 ELSE 0 END, " +
                        "b = CASE WHEN b >= 140 THEN 255 ELSE 0 END");
    
                // Récupérer les données de la table UNEIMAGE
                ResultSet resultSet = statement.executeQuery("SELECT x, y, r, g, b FROM UNEIMAGE");
    
                // Taille de l'image
                int width = 500;
                int height = 391;
    
                // Listes pour stocker les données
                int[] x = new int[width * height];
                int[] y = new int[width * height];
                int[] r = new int[width * height];
                int[] g = new int[width * height];
                int[] b = new int[width * height];
    
                int index = 0;
                while (resultSet.next()) {
                    x[index] = resultSet.getInt("x");
                    y[index] = resultSet.getInt("y");
                    r[index] = resultSet.getInt("r");
                    g[index] = resultSet.getInt("g");
                    b[index] = resultSet.getInt("b");
                    index++;
                }
    
                // Chemin du fichier de sortie
                String filePath = "./ResultatsImage/seuillage.png";
                JNIImageHandler.sauveImage(x, y, r, g, b, width, height, filePath);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    public static void seuillageHysteresis(Connection connexion) throws SQLException {
        try {
            // Définition des seuils haut et bas
            int seuilBas = 100;
            int seuilHaut = 160;
    
            // Mettre à jour les valeurs des pixels en utilisant une seule requête
            try (Statement statement = connexion.createStatement()) {
                statement.executeUpdate("UPDATE UNEIMAGE SET " +
                        "r = CASE " +
                        "       WHEN r > " + seuilHaut + " THEN 255 " +
                        "       WHEN r <= " + seuilBas + " THEN 0 " +
                        "       ELSE 0 " +
                        "     END, " +
                        "g = CASE " +
                        "       WHEN g > " + seuilHaut + " THEN 255 " +
                        "       WHEN g <= " + seuilBas + " THEN 0 " +
                        "       ELSE 0 " +
                        "     END, " +
                        "b = CASE " +
                        "       WHEN b > " + seuilHaut + " THEN 255 " +
                        "       WHEN b <= " + seuilBas + " THEN 0 " +
                        "       ELSE 0 " +
                        "     END " +
                        "WHERE EXISTS (SELECT 1 FROM UNEIMAGE i2 " +
                        "             WHERE (i2.r > " + seuilBas + " OR i2.g > " + seuilBas + " OR i2.b > " + seuilBas + ") " +
                        "               AND (i2.x = UNEIMAGE.x - 1 OR i2.x = UNEIMAGE.x + 1 OR i2.y = UNEIMAGE.y - 1 OR i2.y = UNEIMAGE.y + 1))");
    
                // Récupérer les données de la table UNEIMAGE
                ResultSet resultSet = statement.executeQuery("SELECT x, y, r, g, b FROM UNEIMAGE");
    
                // Taille de l'image
                int width = 500;
                int height = 391;
    
                // Listes pour stocker les données
                int[] x = new int[width * height];
                int[] y = new int[width * height];
                int[] r = new int[width * height];
                int[] g = new int[width * height];
                int[] b = new int[width * height];
    
                int index = 0;
                while (resultSet.next()) {
                    x[index] = resultSet.getInt("x");
                    y[index] = resultSet.getInt("y");
                    r[index] = resultSet.getInt("r");
                    g[index] = resultSet.getInt("g");
                    b[index] = resultSet.getInt("b");
                    index++;
                }
    
                // Chemin du fichier de sortie
                String filePath = "./ResultatsImage/seuillagehysterisis.png";
                JNIImageHandler.sauveImage(x, y, r, g, b, width, height, filePath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    public static void downscaling(Connection connexion, String pathInput, String pathOutput, int tailledownscaling) throws SQLException {
        try {
            // Créer la table TEMP_IMAGE si elle n'existe pas déjà
            try (Statement statement = connexion.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS TEMP_IMAGE (x INT, y INT, r INT, g INT, b INT)");
            
    
            // Insérer les données de l'image d'entrée dans TEMP_IMAGE
            BufferedImage image = ImageIO.read(new File(pathInput));
            int width = image.getWidth();
            int height = image.getHeight();
    
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
    
                    String query = "INSERT INTO TEMP_IMAGE (x, y, r, g, b) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connexion.prepareStatement(query)) {
                        preparedStatement.setInt(1, x);
                        preparedStatement.setInt(2, y);
                        preparedStatement.setInt(3, red);
                        preparedStatement.setInt(4, green);
                        preparedStatement.setInt(5, blue);
                        preparedStatement.executeUpdate();
                    }
                }
            }
    
            // Créer une table temporaire pour les pixels agrégés
            String createAggregatedTable = "CREATE TEMPORARY TABLE IF NOT EXISTS AGGREGATED_IMAGE AS " +
                    "SELECT " +
                    "FLOOR(X / " + tailledownscaling + ") AS newX, " +
                    "FLOOR(Y / " + tailledownscaling + ") AS newY, " +
                    "AVG(CAST(R AS INT)) AS avgR, " +
                    "AVG(CAST(G AS INT)) AS avgG, " +
                    "AVG(CAST(B AS INT)) AS avgB " +
                    "FROM TEMP_IMAGE " +
                    "GROUP BY newX, newY";

            statement.executeUpdate(createAggregatedTable);
    
            // Récupérer les données de l'image agrégée
            ResultSet resultSet = statement.executeQuery("SELECT newX, newY, avgR, avgG, avgB FROM AGGREGATED_IMAGE ORDER BY newY, newX");
    
            // Calcul de la nouvelle taille de l'image
            int newWidth = (int) Math.ceil((double) width / tailledownscaling);
            int newHeight = (int) Math.ceil((double) height / tailledownscaling);
    
            int[] x = new int[newWidth * newHeight];
            int[] y = new int[newWidth * newHeight];
            int[] r = new int[newWidth * newHeight];
            int[] g = new int[newWidth * newHeight];
            int[] b = new int[newWidth * newHeight];
    
            int index = 0;
            while (resultSet.next()) {
                x[index] = resultSet.getInt("newX");
                y[index] = resultSet.getInt("newY");
                r[index] = resultSet.getInt("avgR");
                g[index] = resultSet.getInt("avgG");
                b[index] = resultSet.getInt("avgB");
                index++;
            }
            // Sauvegarder l'image résultante
            JNIImageHandler.sauveImage(x, y, r, g, b, newWidth, newHeight, pathOutput);
            System.out.println("Image downscaling réussi : " + pathOutput);
        }} catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
}
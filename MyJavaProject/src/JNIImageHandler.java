public class JNIImageHandler {

    public static native void sauveImage(String tableName, String filePath);

    static {
        System.loadLibrary("ImageHandler");
    }

    public static void saveImage(String tableName, String filePath) {
        sauveImage(tableName, filePath);
    }
}
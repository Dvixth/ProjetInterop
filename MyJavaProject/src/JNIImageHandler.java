public class JNIImageHandler {
    public static native void sauveImage(int[] x, int[] y, int[] r, int[] g, int[] b, int width, int height,String filepath);

    static {
        System.load("/home/smail/Bureau/ProjetInterop/build/libmylibrary.so");
    }
    
}

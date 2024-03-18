#include <iostream>
#include <vector>
#include <jni.h>
#include <CImg.h>

extern "C" JNIEXPORT void JNICALL Java_JNIImageHandler_sauveImage(JNIEnv *env, jclass cls, jintArray xArray, jintArray yArray, jintArray rArray, jintArray gArray, jintArray bArray, jint width, jint height,jstring filepathString) {
  
    jint *xData = env->GetIntArrayElements(xArray, nullptr);
    jint *yData = env->GetIntArrayElements(yArray, nullptr);
    jint *rData = env->GetIntArrayElements(rArray, nullptr);
    jint *gData = env->GetIntArrayElements(gArray, nullptr);
    jint *bData = env->GetIntArrayElements(bArray, nullptr);

    cimg_library::CImg<unsigned char> image(width, height, 1, 3);
    const char *filepath = env->GetStringUTFChars(filepathString, nullptr); // Convert jstring to const char*

    for (int i = 0; i < width * height; ++i) {
        int x = xData[i];
        int y = yData[i];
        int r = rData[i];
        int g = gData[i];
        int b = bData[i];

        r = std::min(std::max(r, 0), 255);
        g = std::min(std::max(g, 0), 255);
        b = std::min(std::max(b, 0), 255);

        image(x, y, 0, 0) = r;
        image(x, y, 0, 1) = g;
        image(x, y, 0, 2) = b;
    }

    env->ReleaseIntArrayElements(xArray, xData, JNI_ABORT);
    env->ReleaseIntArrayElements(yArray, yData, JNI_ABORT);
    env->ReleaseIntArrayElements(rArray, rData, JNI_ABORT);
    env->ReleaseIntArrayElements(gArray, gData, JNI_ABORT);
    env->ReleaseIntArrayElements(bArray, bData, JNI_ABORT);

    image.save_png(filepath); // Save the image using the filepath

    env->ReleaseStringUTFChars(filepathString, filepath); // Release the string resources

    
}

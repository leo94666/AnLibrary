package com.top.plateocr;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

//javah -jni com.top.plateocr.PlateRecognition
public class PlateRecognition {

    static {
        System.loadLibrary("hyperlpr");
    }
    public static long handle;

    public static void init(Context context){
        String assetPath = "pr";
        String sdcardPath = Environment.getExternalStorageDirectory()
                + File.separator + assetPath;
        copyFilesFromAssets(context, assetPath, sdcardPath);
        String cascade_filename  =  sdcardPath
                + File.separator+"cascade.xml";
        String finemapping_prototxt  =  sdcardPath
                + File.separator+"HorizonalFinemapping.prototxt";
        String finemapping_caffemodel  =  sdcardPath
                + File.separator+"HorizonalFinemapping.caffemodel";
        String segmentation_prototxt =  sdcardPath
                + File.separator+"Segmentation.prototxt";
        String segmentation_caffemodel =  sdcardPath
                + File.separator+"Segmentation.caffemodel";
        String character_prototxt =  sdcardPath
                + File.separator+"CharacterRecognization.prototxt";
        String character_caffemodel=  sdcardPath
                + File.separator+"CharacterRecognization.caffemodel";
        String segmentationfree_prototxt =  sdcardPath
                + File.separator+"SegmenationFree-Inception.prototxt";
        String segmentationfree_caffemodel=  sdcardPath
                + File.separator+"SegmenationFree-Inception.caffemodel";
        handle  =  PlateRecognition.InitPlateRecognizer(
                cascade_filename,
                finemapping_prototxt,finemapping_caffemodel,
                segmentation_prototxt,segmentation_caffemodel,
                character_prototxt,character_caffemodel,
                segmentationfree_prototxt,segmentationfree_caffemodel
        );

    }


    public static void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.mkdir()) {
                    Log.d("mkdir","can't make folder");
                }
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static native long InitPlateRecognizer(
            String casacde_detection,
            String fine_mapping_proto_txt,
            String fine_mapping_caffe_model,
            String segmentation_proto_txt,
            String segmentation_caffe_model,
            String char_Recognization_proto,
            String char_recognization_caffe_model,
            String segmentation_free_proto,
            String segmentation_free_caffe_model
    );

    public static native void ReleasePlateRecognizer(long  object);
    public static native String SimpleRecognization(long  inputMat,long object);
    public static native PlateInfo PlateInfoRecognization(long  inputMat,long object);


}

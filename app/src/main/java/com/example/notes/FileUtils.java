package com.example.notes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static final String DIRECTORY_NAME = "images";

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap, String imageName){
        File directory = new File(context.getFilesDir(), DIRECTORY_NAME);
        if (!directory.exists()){
            directory.mkdirs();
        }

        File imageFile = new File(directory, imageName+".jpg");
        try(FileOutputStream fos = new FileOutputStream(imageFile)){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

        return imageFile.getAbsolutePath();
    }

    public static Bitmap loadImageFromInternalStorage(String filePath){
        Bitmap bitmap = null;
        try{
            File imageFile = new File(filePath);
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }





}

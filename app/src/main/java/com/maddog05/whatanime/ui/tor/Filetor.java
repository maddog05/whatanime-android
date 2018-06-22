package com.maddog05.whatanime.ui.tor;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Filetor {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String saveSampleImageLocal(Bitmap bitmap, String folder, String name) {
        String response;
        try {
            //FOLDER
            //folder "images/requests"
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folder);
            path.mkdirs();
            //FILE
            File file = new File(path, "/" + name + ".png");
            if (file.exists()) {
                response = file.getAbsolutePath();
            } else {
                FileOutputStream outputStream = new FileOutputStream(file);
                boolean isCompleted = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (isCompleted)
                    response = file.getAbsolutePath();
                else
                    response = "";
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
            response = "";
        }
        return response;
    }

    public static String getRequestImageLocalName(long id) {
        return "requestN" + id;
    }
}

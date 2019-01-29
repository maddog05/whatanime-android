package com.maddog05.whatanime.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import com.maddog05.maddogutilities.callback.Callback;
import java.io.ByteArrayOutputStream;

public class ImageEncoder {
    private static final String ERROR_RESPONSE = "";
    private Bitmap bitmap;
    private Callback<String> callback;

    private ImageEncoder() {
    }

    public static ImageEncoder with(Bitmap bitmap) {
        ImageEncoder encoder = new ImageEncoder();
        encoder.bitmap = bitmap;
        return encoder;
    }

    public ImageEncoder callback(Callback<String> callback) {
        this.callback = callback;
        return this;
    }

    public void encode() {
        (new AsyncTask<Bitmap, Void, String>() {
            protected String doInBackground(Bitmap... bitmaps) {
                if (ImageEncoder.this.bitmap == null) {
                    return "";
                } else if (ImageEncoder.this.callback == null) {
                    return "";
                } else {
                    String response;
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        response = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    } catch (Exception var5) {
                        response = "";
                    }

                    return response;
                }
            }

            protected void onPostExecute(String encodedBitmap) {
                ImageEncoder.this.callback.done(encodedBitmap);
            }
        }).execute(new Bitmap[]{this.bitmap});
    }
}


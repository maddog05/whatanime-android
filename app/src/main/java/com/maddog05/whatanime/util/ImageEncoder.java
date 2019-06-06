package com.maddog05.whatanime.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;

import com.maddog05.maddogutilities.callback.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static Bitmap getBitmapCompressed(Context context, Uri photoUri, int maxDimensionPixels) {
        Bitmap srcBitmap = null;
        try {
            InputStream is = context.getContentResolver().openInputStream(photoUri);
            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, dbo);
            if (is != null)
                is.close();

            int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(context, photoUri);

            if (orientation == 90 || orientation == 270) {
                rotatedWidth = dbo.outHeight;
                rotatedHeight = dbo.outWidth;
            } else {
                rotatedWidth = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            }

            is = context.getContentResolver().openInputStream(photoUri);

            if (rotatedWidth > maxDimensionPixels || rotatedHeight > maxDimensionPixels) {
                float widthRatio = ((float) rotatedWidth) / ((float) maxDimensionPixels);
                float heightRatio = ((float) rotatedHeight) / ((float) maxDimensionPixels);
                float maxRatio = Math.max(widthRatio, heightRatio);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(is, null, options);
            } else {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            if (is != null)
                is.close();

            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return srcBitmap;
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        int response = cursor.getInt(0);
        cursor.close();
        return response;
    }
}


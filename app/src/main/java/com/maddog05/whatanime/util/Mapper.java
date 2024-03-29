package com.maddog05.whatanime.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maddog05.maddogutilities.number.Numbers;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.ChangelogItem;
import com.maddog05.whatanime.core.entity.output.OutputGetQuota;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by andreetorres on 24/09/17.
 */

public class Mapper {

    public static String parseLocalVideoPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String response = cursor.getString(column_index);
            cursor.close();
            return response;
        } else {
            return null;
        }
    }

    private static Bitmap scaleToFitWidth(Bitmap b, int width) {
        /*int origWidth = b.getHeight();
        int origHeight = b.getWidth();

        final int destWidth = width;//or the width you need

        int destHeight = origHeight / (origWidth / destWidth);
        Log.d("#Andree", "original width = " + origWidth);
        Log.d("#Andree", "original height = " + origHeight);
        Log.d("#Andree", "resized width = " + destWidth);
        Log.d("#Andree", "resized height = " + destHeight);
        return Bitmap.createScaledBitmap(b, destWidth, destHeight, false);*/

        //MODIF
        int originWidth = b.getWidth();
        int originHeight = b.getHeight();
        int destinyWidth = width;
        float factor = (float) destinyWidth / (float) originWidth;
        int destinyHeight = (int) (originHeight * factor);

        return Bitmap.createScaledBitmap(b, 1280, 720, true);

        //ORIGINAL
        //float factor = width / (float) b.getWidth();
        //return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    public static byte[] parseBitmapToByteArray(Bitmap bitmap, int maxDimensionPixels) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap newBitmap = scaleToFitWidth(bitmap, maxDimensionPixels);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap parseVideoFrameFromSelectFrame(Intent data) {
        Bitmap bitmap;
        try {
            byte[] byteArray = data.getExtras().getByteArray(C.Extras.VIDEO_FRAME_BITMAP);
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            bitmap = null;
        }
        return bitmap;
    }

    public static String parseSecondToHourTimeSeconds(double seconds) {
        String response;
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int newSeconds = (int) (seconds % 60);
        response = Numbers.formatIntegerTwoNumbers(hours) + ":";
        response += Numbers.formatIntegerTwoNumbers(minutes) + ":";
        response += Numbers.formatIntegerTwoNumbers(newSeconds);
        return response;
    }

    public static String parsePercentageSimilarity(double similarity) {
        double _similarity = similarity * 100.00;
        String response = Numbers.formatDoubleTwoDecimals(_similarity);
        response += "%";
        return response;
    }

    public static String parseEpisodeNumber(Context context, String episode) {
        if (episode == null || episode.isEmpty()) {
            return context.getString(R.string.tag_no_episode_number);
        }
        int response;
        try {
            response = Integer.parseInt(episode);
        } catch (NumberFormatException nfE) {
            nfE.printStackTrace();
            response = -1;
        }
        if (response != -1) {
            return context.getString(R.string.tag_episode_number) + C.SPACE + Numbers.formatIntegerTwoNumbers(response);
        } else {
            return episode;
        }
    }

    private static JsonObject stringToJsonObject(String toJson) {
        return JsonParser.parseString(toJson).getAsJsonObject();
    }

    public static OutputGetQuota parseGetQuota(JsonObject json) {
        OutputGetQuota outputGetQuota = new OutputGetQuota();
        outputGetQuota.setSearchQuota(elementInt(json.get("quotaUsed"), 0));
        outputGetQuota.setSearchsPerMinute(elementInt(json.get("quota"), 0));
        return outputGetQuota;
    }

    public static List<ChangelogItem> parseChangelog(JsonArray json) {
        List<ChangelogItem> response = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            JsonObject jsonItem = json.get(i).getAsJsonObject();
            JsonArray jsonChanges = jsonItem.get("changes").getAsJsonArray();
            for (int j = 0; j < jsonChanges.size(); j++) {
                JsonObject jsonChange = jsonChanges.get(j).getAsJsonObject();
                ChangelogItem item = new ChangelogItem();
                item.versionName = jsonItem.get("name").getAsString();
                item.changeType = jsonChange.get("type").getAsString();
                item.descriptionType = jsonChange.get("description").getAsString();
                response.add(item);
            }
        }
        return response;
    }

    public static JsonArray elementArray(JsonElement element) {
        if (element != null && !element.isJsonNull() && element.isJsonArray())
            return element.getAsJsonArray();
        else
            return new JsonArray();
    }

    public static int elementInt(JsonElement element, int defaultValue) {
        if (element != null && !element.isJsonNull())
            return element.getAsInt();
        else
            return defaultValue;
    }

    public static String elementString(JsonElement element, String defaultValue) {
        if (element != null && !element.isJsonNull())
            return element.getAsString();
        else
            return defaultValue;
    }

    public static boolean elementBoolean(JsonElement element, boolean defaultValue) {
        if (element != null && !element.isJsonNull())
            return element.getAsBoolean();
        else
            return defaultValue;
    }

    public static double elementDouble(JsonElement element, double defaultValue) {
        if (element != null && !element.isJsonNull())
            return element.getAsDouble();
        else
            return defaultValue;
    }
}

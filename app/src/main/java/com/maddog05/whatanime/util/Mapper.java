package com.maddog05.whatanime.util;

import android.content.Context;
import android.os.Build;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maddog05.maddogutilities.number.Numbers;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.SearchDetail;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by andreetorres on 24/09/17.
 */

public class Mapper {
    public static String parseSecondToHourTimeSeconds(double seconds) {
        int realSeconds;
        String response = "";
        String _seconds = String.valueOf(seconds);
        if (_seconds.contains(".")) {
            int index = _seconds.indexOf(".");
            String _s = _seconds.substring(0, index - 1);
            realSeconds = Integer.parseInt(!_s.isEmpty() ? _s : "0");
        } else {
            realSeconds = Integer.parseInt(_seconds);
        }

        int hourTime = realSeconds / (60 * 60);
        int minuteTime = realSeconds / 60;
        int secondTime = realSeconds % 60;

        response = Numbers.formatIntegerTwoNumbers(hourTime) + ":";
        response += Numbers.formatIntegerTwoNumbers(minuteTime) + ":";
        response += Numbers.formatIntegerTwoNumbers(secondTime);

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

    public static String getVideoUrl(SearchDetail.Doc doc) {
        String encodeFormat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? StandardCharsets.UTF_8.toString() : C.EMPTY;
        String response = "https://whatanime.ga/preview.php?season=";
        response += _encode(doc.season, encodeFormat) + "&anime=";
        response += _encode(doc.anime, encodeFormat) + "&file=";
        response += _encode(doc.fileName, encodeFormat) + "&t=";
        response += _encode(String.valueOf(doc.atTime), encodeFormat) + "&token=";
        response += _encode(doc.tokenThumb, encodeFormat);
        return response;
    }

    public static String getImageUrl(SearchDetail.Doc doc) {
        String encodeFormat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? StandardCharsets.UTF_8.toString() : C.EMPTY;
        String response = "https://whatanime.ga/thumbnail.php?season=";
        response += _encode(doc.season, encodeFormat) + "&anime=";
        response += _encode(doc.anime, encodeFormat) + "&file=";
        response += _encode(doc.fileName, encodeFormat) + "&t=";
        response += _encode(String.valueOf(doc.atTime), encodeFormat) + "&token=";
        response += _encode(doc.tokenThumb, encodeFormat);
        return response;
    }

    private static String _encode(String toEncode, String charset) {
        if (charset.isEmpty()) {
            return URLEncoder.encode(toEncode);
        } else {
            try {
                return URLEncoder.encode(toEncode, charset);
            } catch (Exception e) {
                e.printStackTrace();
                return URLEncoder.encode(toEncode);
            }
        }
    }

    private static JsonObject stringToJsonObject(String toJson) {
        return new JsonParser().parse(toJson).getAsJsonObject();
    }

    public static SearchDetail parseSearchDetail(JsonObject json) {
        SearchDetail searchDetail = new SearchDetail();

        searchDetail.rawDocsCount = new ArrayList<>();
        JsonArray arrayRawDocsCount = json.get("RawDocsCount").getAsJsonArray();
        for (int i = 0; i < arrayRawDocsCount.size(); i++)
            searchDetail.rawDocsCount.add(arrayRawDocsCount.get(i).getAsInt());

        searchDetail.rawDocsSearchTime = new ArrayList<>();
        JsonArray arrayRawDocsSearchTime = json.get("RawDocsSearchTime").getAsJsonArray();
        for (int i = 0; i < arrayRawDocsSearchTime.size(); i++)
            searchDetail.rawDocsSearchTime.add(arrayRawDocsSearchTime.get(i).getAsInt());

        searchDetail.reRankSearchTime = new ArrayList<>();
        JsonArray arrayReRankSearchTime = json.get("ReRankSearchTime").getAsJsonArray();
        for (int i = 0; i < arrayReRankSearchTime.size(); i++)
            searchDetail.reRankSearchTime.add(arrayReRankSearchTime.get(i).getAsInt());

        searchDetail.cacheHit = json.get("CacheHit").getAsBoolean();
        searchDetail.trial = json.get("trial").getAsInt();
        searchDetail.quota = json.get("quota").getAsInt();
        searchDetail.expire = json.get("expire").getAsInt();

        searchDetail.docs = new ArrayList<>();
        JsonArray arrayDocs = json.get("docs").getAsJsonArray();
        for (int i = 0; i < arrayDocs.size(); i++) {
            JsonObject _doc = arrayDocs.get(i).getAsJsonObject();
            SearchDetail.Doc doc = new SearchDetail.Doc();

            doc.fromTime = _doc.get("from").getAsDouble();
            doc.toTime = _doc.get("to").getAsDouble();
            doc.atTime = _doc.get("at").getAsDouble();
            doc.episode = _doc.get("episode").getAsString();
            doc.similarity = _doc.get("similarity").getAsDouble();
            JsonElement _b = _doc.get("title");
            doc.japaneseTitle = _b != null && !_b.isJsonNull() ? _b.getAsString() : C.EMPTY;
            JsonElement _a = _doc.get("anilist_id");
            doc.anilistId = _a != null && !_a.isJsonNull() ? _a.getAsInt() : C.INTEGER_NONE;
            doc.englishTitle = _doc.get("title_english").getAsString();

            doc.synonyms = new ArrayList<>();
            JsonArray arraySynonyms = _doc.get("synonyms").getAsJsonArray();
            for (int j = 0; j < arraySynonyms.size(); j++)
                doc.synonyms.add(arraySynonyms.get(j).getAsString());

            doc.romanjiTitle = _doc.get("title_romaji").getAsString();
            doc.season = _doc.get("season").getAsString();
            doc.anime = _doc.get("anime").getAsString();
            doc.fileName = _doc.get("filename").getAsString();
            doc.tokenThumb = _doc.get("tokenthumb").getAsString();

            searchDetail.docs.add(doc);
        }

        return searchDetail;
    }
}

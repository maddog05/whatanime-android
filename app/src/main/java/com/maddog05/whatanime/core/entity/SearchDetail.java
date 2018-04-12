package com.maddog05.whatanime.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreetorres on 24/09/17.
 */

public class SearchDetail implements Parcelable {
    public List<Integer> rawDocsCount; //Number of frames compared for each trial
    public List<Integer> rawDocsSearchTime; //Time taken to retrieve the frames for each trial
    public List<Integer> reRankSearchTime; //Time taken to compare the frames for each trial
    public Boolean cacheHit; //Whether the search result is cached. (Results are cached by extraced image feature)
    public Integer trial; //Number of times searched
    public Integer quota; //Number of search quota remaining
    public Integer expire; //Time until quota resets (seconds)
    public List<Doc> docs;

    public static class Doc implements Parcelable {
        public Double fromTime; //starting matching scene
        public Double toTime; //finish matching scene
        public Double atTime; //exact time scene
        public String episode; //"22", "OVA/OAD", "Special",<EMPTY>
        public Double similarity; //0-1 range
        public Integer anilistId; //-1 == null
        public String japaneseTitle;
        public String englishTitle;
        public String romanjiTitle;
        public List<String> synonyms; //alternate english titles
        public String season; //(Movie, OVA, Others, Sukebei, 1970-1989, 1990-1999, 2000-01, 2017-01, etc
        public String anime; //The folder where the file is located (This may act as a fallback when title is not found)
        public String fileName; //The filename of file where the match is found
        public String tokenThumb; //A token for generating preview
        public Integer myAnimeListId;//The matching MyAnimeList ID
        public Boolean isHentai;//Whether the anime is hentai

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.fromTime);
            dest.writeValue(this.toTime);
            dest.writeValue(this.atTime);
            dest.writeString(this.episode);
            dest.writeValue(this.similarity);
            dest.writeValue(this.anilistId);
            dest.writeString(this.japaneseTitle);
            dest.writeString(this.englishTitle);
            dest.writeString(this.romanjiTitle);
            dest.writeStringList(this.synonyms);
            dest.writeString(this.season);
            dest.writeString(this.anime);
            dest.writeString(this.fileName);
            dest.writeString(this.tokenThumb);
        }

        public Doc() {
        }

        protected Doc(Parcel in) {
            this.fromTime = (Double) in.readValue(Double.class.getClassLoader());
            this.toTime = (Double) in.readValue(Double.class.getClassLoader());
            this.atTime = (Double) in.readValue(Double.class.getClassLoader());
            this.episode = in.readString();
            this.similarity = (Double) in.readValue(Double.class.getClassLoader());
            this.anilistId = (Integer) in.readValue(Integer.class.getClassLoader());
            this.japaneseTitle = in.readString();
            this.englishTitle = in.readString();
            this.romanjiTitle = in.readString();
            this.synonyms = in.createStringArrayList();
            this.season = in.readString();
            this.anime = in.readString();
            this.fileName = in.readString();
            this.tokenThumb = in.readString();
        }

        public static final Creator<Doc> CREATOR = new Creator<Doc>() {
            @Override
            public Doc createFromParcel(Parcel source) {
                return new Doc(source);
            }

            @Override
            public Doc[] newArray(int size) {
                return new Doc[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.rawDocsCount);
        dest.writeList(this.rawDocsSearchTime);
        dest.writeList(this.reRankSearchTime);
        dest.writeValue(this.cacheHit);
        dest.writeValue(this.trial);
        dest.writeValue(this.quota);
        dest.writeValue(this.expire);
        dest.writeList(this.docs);
    }

    public SearchDetail() {
    }

    protected SearchDetail(Parcel in) {
        this.rawDocsCount = new ArrayList<Integer>();
        in.readList(this.rawDocsCount, Integer.class.getClassLoader());
        this.rawDocsSearchTime = new ArrayList<Integer>();
        in.readList(this.rawDocsSearchTime, Integer.class.getClassLoader());
        this.reRankSearchTime = new ArrayList<Integer>();
        in.readList(this.reRankSearchTime, Integer.class.getClassLoader());
        this.cacheHit = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.trial = (Integer) in.readValue(Integer.class.getClassLoader());
        this.quota = (Integer) in.readValue(Integer.class.getClassLoader());
        this.expire = (Integer) in.readValue(Integer.class.getClassLoader());
        this.docs = new ArrayList<Doc>();
        in.readList(this.docs, Doc.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchDetail> CREATOR = new Parcelable.Creator<SearchDetail>() {
        @Override
        public SearchDetail createFromParcel(Parcel source) {
            return new SearchDetail(source);
        }

        @Override
        public SearchDetail[] newArray(int size) {
            return new SearchDetail[size];
        }
    };
}

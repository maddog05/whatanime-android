package com.maddog05.whatanime.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.maddogutilities.image.ImageLoader;
import com.maddog05.maddogutilities.logger.Logger2;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.Logic;
import com.maddog05.whatanime.core.entity.SearchDetail;
import com.maddog05.whatanime.util.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreetorres on 24/09/17.
 */

public class AdapterSearchResults extends RecyclerView.Adapter<AdapterSearchResults.ASRVH> {

    private List<SearchDetail.Doc> docs;
    private Callback<SearchDetail.Doc> callback;
    private Context context;

    public AdapterSearchResults(Context context) {
        this.context = context;
        this.docs = new ArrayList<>();
    }

    public void setDocs(List<SearchDetail.Doc> docs) {
        this.docs.clear();
        if (docs != null && docs.size() > 0) {
            for (int i = 0; i < docs.size(); i++) {
                this.docs.add(docs.get(i));
            }
        }
    }

    public void setCallbackItemClick(Callback<SearchDetail.Doc> callback) {
        this.callback = callback;
    }

    @Override
    public ASRVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ASRVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final ASRVH holder, int position) {
        SearchDetail.Doc doc = docs.get(holder.getAdapterPosition());
        holder.titleTv.setText(doc.romanjiTitle);
        holder.episodeTv.setText(Mapper.parseEpisodeNumber(context, doc.episode));
        holder.similarityTv.setText(Mapper.parsePercentageSimilarity(doc.similarity));
        holder.timeTv.setText(Mapper.parseSecondToHourTimeSeconds(doc.atTime));
        ImageLoader imageLoader = Logic.imageLoader(context);
        imageLoader.with(context)
                .placeholder(R.drawable.ic_photo)
                .load(Mapper.getImageUrl(doc))
                .target(holder.photoIv)
                .callback(new Callback<Boolean>() {
                    @Override
                    public void done(Boolean isCompleted) {
                        Logger2.get().d("#Adapter", "isCompleted = " + isCompleted);
                        if (isCompleted) {
                            holder.photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }
                })
                .start();
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    public class ASRVH extends RecyclerView.ViewHolder {
        public AppCompatImageView photoIv;
        public AppCompatTextView titleTv, episodeTv, timeTv, similarityTv;

        public ASRVH(View itemView) {
            super(itemView);
            photoIv = itemView.findViewById(R.id.iv_item_photo);
            titleTv = itemView.findViewById(R.id.tv_item_title);
            episodeTv = itemView.findViewById(R.id.tv_item_episode);
            timeTv = itemView.findViewById(R.id.tv_item_time);
            similarityTv = itemView.findViewById(R.id.tv_item_similarity);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AdapterSearchResults.this.callback != null)
                        AdapterSearchResults.this.callback.done(docs.get(getAdapterPosition()));
                }
            });
        }
    }
}

package com.maddog05.whatanime.ui.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.entity.output.SearchDetail
import com.maddog05.whatanime.core.image.GlideLoader
import com.maddog05.whatanime.util.Mapper

class AdapterMain(val context: Context, val docs: MutableList<SearchDetail.Doc>, val listener: OnDocClickListener) : RecyclerView.Adapter<AdapterMain.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_result, parent, false))
    }

    override fun getItemCount(): Int = docs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = docs[holder.adapterPosition]
        holder.titleTv.text = doc.romanjiTitle
        holder.episodeTv.text = Mapper.parseEpisodeNumber(context, doc.episode)
        holder.similarityTv.text = Mapper.parsePercentageSimilarity(doc.similarity)
        holder.timeTv.text = Mapper.parseSecondToHourTimeSeconds(doc.atTime)
        val imageLoader: GlideLoader = GlideLoader.create()
        imageLoader.with(context)
                .placeholder(R.drawable.ic_photo)
                .load(Mapper.getImageUrl(doc))
                .target(holder.photoIv)
                .callback { isCompleted ->
                    if (isCompleted)
                        holder.photoIv.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                .start()
        holder.itemView.setOnClickListener { listener.onDocClicked(doc) }
    }

    interface OnDocClickListener {
        fun onDocClicked(doc: SearchDetail.Doc)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var photoIv: AppCompatImageView = itemView.findViewById(R.id.iv_item_photo)
        var titleTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_title)
        var episodeTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_episode)
        var timeTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_time)
        var similarityTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_similarity)
    }
}
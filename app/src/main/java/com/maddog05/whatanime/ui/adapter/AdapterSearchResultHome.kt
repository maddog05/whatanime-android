package com.maddog05.whatanime.ui.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.maddogutilities.image.ImageLoader
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.Logic
import com.maddog05.whatanime.core.entity.ResponseEntity
import com.maddog05.whatanime.util.Mapper

class AdapterSearchResultHome(val context: Context) : RecyclerView.Adapter<AdapterSearchResultHome.ASRVH>() {

    private val items = mutableListOf<ResponseEntity>()

    private lateinit var callback: Callback<ResponseEntity>
    private lateinit var longCallback: Callback<ResponseEntity>

    fun setupData(data: List<ResponseEntity>) {
        this.items.clear()
        this.items.addAll(data)
    }

    fun setClickCallback(callback: Callback<ResponseEntity>) {
        this.callback = callback
    }

    fun setLongClickCallback(callback: Callback<ResponseEntity>) {
        this.longCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ASRVH {
        return ASRVH(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_result, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ASRVH, position: Int) {
        val item = items[holder.adapterPosition]
        holder.titleTv.text = item.name
        holder.episodeTv.text = Mapper.parseEpisodeNumber(context, item.episode)
        holder.similarityTv.text = Mapper.parsePercentageSimilarity(item.similarity)
        holder.timeTv.text = Mapper.parseSecondToHourTimeSeconds(item.atTime)
        val imageLoader: ImageLoader = Logic.imageLoader(context)
        imageLoader.with(context)
                .placeholder(R.drawable.ic_photo)
                .load(Mapper.getImageUrl(item))
                .target(holder.photoIv)
                .callback { isCompleted ->
                    if (isCompleted)
                        holder.photoIv.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                .start()
        holder.card.setOnClickListener { callback.done(item) }
        holder.card.setOnLongClickListener {
            longCallback.done(item)
            true
        }
    }

    inner class ASRVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: CardView = itemView.findViewById(R.id.card_item_search_result)
        var photoIv: AppCompatImageView = itemView.findViewById(R.id.iv_item_photo)
        var titleTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_title)
        var episodeTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_episode)
        var timeTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_time)
        var similarityTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_similarity)

    }
}
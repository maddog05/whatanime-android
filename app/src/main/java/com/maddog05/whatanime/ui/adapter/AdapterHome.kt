package com.maddog05.whatanime.ui.adapter

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.maddog05.maddogutilities.callback.Callback
import com.maddog05.maddogutilities.image.ImageLoader
import com.maddog05.maddogutilities.string.Strings
import com.maddog05.maddogutilities.view.SquareImageView
import com.maddog05.whatanime.R
import com.maddog05.whatanime.core.Logic
import com.maddog05.whatanime.core.entity.RequestEntity
import com.maddog05.whatanime.util.C
import com.maddog05.whatanime.util.Mapper

class AdapterHome(val context: Context) : RecyclerView.Adapter<AdapterHome.AHVH>() {

    private val items: MutableList<RequestEntity> = mutableListOf()
    private lateinit var callback: Callback<RequestEntity>
    private lateinit var callbackReload: Callback<RequestEntity>

    fun setClickCallback(callback: Callback<RequestEntity>) {
        this.callback = callback
    }

    fun setClickAction(callbackReload: Callback<RequestEntity>) {
        this.callbackReload = callbackReload
    }

    fun setupData(items: List<RequestEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addRequest(entity: RequestEntity) {
        items.add(entity)
        notifyItemInserted(items.count() - 1)
    }

    fun updateRequest(entity: RequestEntity): Int {
        var position = -1
        for (i in 0 until items.size) {
            if (items[i].id == entity.id) {
                items.removeAt(i)
                items.add(i, entity)
                notifyItemChanged(i)
                position = i
                break
            }
        }
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AHVH {
        return AHVH(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_history, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AHVH, position: Int) {
        val item = items[holder.adapterPosition]
        val newDate = Mapper.parseTimelineDate(item.date)
        if (holder.adapterPosition > 0) {
            val previousItem = items.get(holder.adapterPosition - 1)
            val oldDate = Mapper.parseTimelineDate(previousItem.date)
            holder.dateTv.visibility = if (oldDate == newDate) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        holder.dateTv.text = newDate
        holder.hourTv.text = Mapper.parseTimelineHour(context, item.date)
        val pbarVisibility: Int
        val resultsVisibility: Int
        val retryVisibility: Int
        val moreVisibility: Int
        when (item.status) {
            RequestEntity.STATUS_REQUESTING -> {
                pbarVisibility = View.VISIBLE
                resultsVisibility = View.GONE
                retryVisibility = View.GONE
                moreVisibility = View.GONE
            }
            RequestEntity.STATUS_DONE -> {
                pbarVisibility = View.GONE
                resultsVisibility = View.VISIBLE
                retryVisibility = View.GONE
                moreVisibility = View.VISIBLE
            }
            else -> {
                pbarVisibility = View.GONE
                resultsVisibility = View.GONE
                retryVisibility = View.VISIBLE
                moreVisibility = View.GONE
            }
        }
        holder.loadingPbar.visibility = pbarVisibility
        holder.searchResultTv.visibility = resultsVisibility
        holder.retryBtn.visibility = retryVisibility
        holder.moreBtn.visibility = moreVisibility
        if (!C.isActivityFinishing(context)) {
            val imageLoader: ImageLoader = Logic.imageLoader(context)
            imageLoader.with(context)
                    .load(item.imageUrl)
                    .target(holder.photoIv)
                    .callback({ isCompleted ->
                        if (isCompleted)
                            holder.photoIv.scaleType = ImageView.ScaleType.CENTER_CROP
                    })
                    .start()
        }
        holder.photoIv.setOnClickListener { callback.done(item) }
        holder.moreBtn.setOnClickListener { callback.done(item) }
        holder.retryBtn.setOnClickListener { callbackReload.done(item) }
        if (item.status == RequestEntity.STATUS_DONE && item.responses.isNotEmpty()) {
            val response = item.responses[0]
            holder.searchResultTv.text =
                    Strings.CharSequenceStyle()
                            .addBold(Mapper.parsePercentageSimilarity(response.similarity))
                            .addNormal(C.SPACE)
                            .addNormal(response.name)
                            .addNormal(C.SPACE)
                            .addNormal(Mapper.parseEpisodeNumber(context, response.episode))
                            .build()
        }
    }

    class AHVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoIv: SquareImageView = itemView.findViewById(R.id.iv_item_photo)
        val dateTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_date)
        val hourTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_hour)
        val searchResultTv: AppCompatTextView = itemView.findViewById(R.id.tv_item_result)
        val loadingPbar: ProgressBar = itemView.findViewById(R.id.pbar_loading)
        val retryBtn: AppCompatButton = itemView.findViewById(R.id.ib_item_retry)
        val moreBtn: AppCompatButton = itemView.findViewById(R.id.ib_item_expand)
    }
}
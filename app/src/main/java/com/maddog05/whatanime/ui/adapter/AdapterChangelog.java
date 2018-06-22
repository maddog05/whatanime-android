package com.maddog05.whatanime.ui.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maddog05.maddogutilities.string.Strings;
import com.maddog05.whatanime.R;
import com.maddog05.whatanime.core.entity.ChangelogItem;
import com.maddog05.whatanime.util.C;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by andreetorres on 26/01/18.
 */

public class AdapterChangelog extends RecyclerView.Adapter<AdapterChangelog.ACVH> {

    private List<ChangelogItem> items;

    public AdapterChangelog(List<ChangelogItem> items) {
        this.items = new ArrayList<>();
        if (items != null)
            this.items.addAll(items);
    }

    @Override
    public ACVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ACVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_changelog, parent, false));
    }

    @Override
    public void onBindViewHolder(ACVH holder, int position) {
        ChangelogItem item = items.get(holder.getAdapterPosition());
        holder.nameTv.setText(item.versionName);
        holder.typeTv.setText(
                new Strings.CharSequenceStyle()
                        .addBold(item.changeType.toUpperCase())
                        .addNormal(C.SPACE)
                        .addNormal(item.descriptionType)
                        .build());
        int _position = holder.getAdapterPosition();
        if (_position > 0) {
            ChangelogItem previousItem = items.get(_position - 1);
            boolean hideTitle = previousItem.versionName.equals(item.versionName);
            holder.nameTv.setVisibility(hideTitle ? View.GONE : View.VISIBLE);
        }
        else{
            holder.nameTv.setVisibility(View.VISIBLE);
        }
        if (_position < items.size() - 1) {
            ChangelogItem nextItem = items.get(_position + 1);
            boolean hideSpace = nextItem.versionName.equals(item.versionName);
            holder.spaceView.setVisibility(hideSpace ? View.GONE : View.VISIBLE);
        } else {
            holder.spaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ACVH extends RecyclerView.ViewHolder {

        AppCompatTextView nameTv;
        AppCompatTextView typeTv;
        View spaceView;

        public ACVH(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_item_title);
            typeTv = itemView.findViewById(R.id.tv_item_type);
            spaceView = itemView.findViewById(R.id.view_changelog_space);
        }
    }
}

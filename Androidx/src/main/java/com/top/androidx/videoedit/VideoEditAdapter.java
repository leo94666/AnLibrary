package com.top.androidx.videoedit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.top.androidx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 * @version 1.0
 * @className VideoEditAdapter
 * @description VideoEdit 适配器
 * @date 2022/2/15 15:18
 **/
public class VideoEditAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<VideoEditInfo> mData;

    public VideoEditAdapter(Context context) {
        this.mContext = context;
    }

    public void addItemVideoInfo(VideoEditInfo info) {
        if (mData == null) mData = new ArrayList<>();
        mData.add(info);
        notifyItemInserted(mData.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Glide.with(mContext).load("file://" + mData.get(position).getPath()).into(((EditViewHolder) holder).img);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private final class EditViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        EditViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.id_image);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img.getLayoutParams();
            // layoutParams.width = itemW;
            img.setLayoutParams(layoutParams);
        }
    }

}

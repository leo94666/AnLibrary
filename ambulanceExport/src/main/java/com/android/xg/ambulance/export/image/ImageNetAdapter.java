package com.android.xg.ambulance.export.image;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.xg.ambulance.export.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import java.util.List;

/**
 * 自定义适配器，网络图片
 */
public class ImageNetAdapter extends BannerAdapter<DataBean, ImageHolder> {

    public ImageNetAdapter() {
        super(null);
    }


    public void addDates(List<DataBean> datas) {
        if (mDatas.size() != 0) {
            mDatas.clear();
        }
        setDatas(datas);
    }
    public void updateDates(List<DataBean> datas) {
        if (mDatas.size() != 0) {
            mDatas.clear();

        }
        setDatas(datas);
    }


    @Override
    public ImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) BannerUtils.getView(parent, R.layout.banner_image);
        //通过裁剪实现圆角
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            BannerUtils.setBannerRound(imageView, 0);
//        }
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindView(ImageHolder holder, DataBean data, int position, int size) {
        //通过图片加载器实现圆角，你也可以自己使用圆角的imageview，实现圆角的方法很多，自己尝试哈
        Glide.with(holder.itemView)
                .load(data.getImageUrl())
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);
    }

}

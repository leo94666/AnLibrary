package com.android.xg.ambulance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.top.androidx.ratingbar.CBRatingBar
import java.util.*


class ExportsAdapter(
    private val mContext: Context,
    private val mData: MutableList<Exports>?,
) :
    RecyclerView.Adapter<ExportsAdapter.ExportsHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExportsHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_export, parent, false)
        return ExportsHolder(view)
    }

    override fun onBindViewHolder(holder: ExportsHolder, position: Int) {
        holder.tvName.text = mData?.get(position)?.name

        Glide.with(mContext).load(mData?.get(position)?.avatar).into(holder.ivAvatar)

        holder.tvRole.visibility = if (mData?.get(position)?.certification == true) {
            View.VISIBLE
        } else {
            View.GONE
        }

        mData?.get(position)?.score?.toFloat()?.let { holder.cbRatingBar.setStarProgress(it) }



    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    inner class ExportsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: AppCompatTextView = itemView.findViewById(R.id.tv_name)
        var ivAvatar = itemView.findViewById<AppCompatImageView>(R.id.iv_avatar)
        var tvRole = itemView.findViewById<AppCompatTextView>(R.id.tv_role)
        var cbRatingBar = itemView.findViewById<CBRatingBar>(R.id.rating_bar)


    }
}

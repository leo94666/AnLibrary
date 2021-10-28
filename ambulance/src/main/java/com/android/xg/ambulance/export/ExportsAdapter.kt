package com.android.xg.ambulance.export

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.xg.ambulance.R
import com.bumptech.glide.Glide
import com.top.androidx.ratingbar.CBRatingBar


class ExportsAdapter(
    private val mContext: Context
) :
    RecyclerView.Adapter<ExportsAdapter.ExportsHolder?>() {

    private lateinit var mData: MutableList<Exports>

    private  var mOnSelectedListener: OnSelectedListener?=null
    interface OnSelectedListener {
        fun onSelectedItem(position: Exports, isChecked:Boolean): Boolean
    }

    fun setOnSelectedListener(mOnSelectedListener: OnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener
    }


    init {
        this.mData= mutableListOf()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExportsHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_export, parent, false)
        return ExportsHolder(view)
    }

    override fun onBindViewHolder(holder: ExportsHolder, position: Int) {
        holder.tvName.text = mData[position].name

        Glide.with(mContext)
            .load(mData[position].avatar)
            .circleCrop()
            .into(holder.ivAvatar)

        holder.tvRole.visibility = if (mData[position].certification) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.itemView.setOnClickListener {
            //Toast.makeText(mContext,"UUUUUUU",Toast.LENGTH_SHORT).show()
            val checked = holder.checkBox.isChecked
            if (checked){
                //已经是选中的
                holder.checkBox.isChecked=false
                mOnSelectedListener?.onSelectedItem(mData[position],false)
            }else{
                holder.checkBox.isChecked=true
                mOnSelectedListener?.onSelectedItem(mData[position],true)
            }
        }

        mData[position].score.toFloat().let { holder.cbRatingBar.setStarProgress(it) }


    }

    override fun getItemCount(): Int {
        return if (mData.isNullOrEmpty()) {
            0
        } else {
            mData.size
        }
    }

    public fun addData(mData: MutableList<Exports>) {
        if (this.mData.isNullOrEmpty()){
            this.mData= mutableListOf()
        }
        this.mData.addAll(mData)
        notifyDataSetChanged()

    }

    inner class ExportsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: AppCompatTextView = itemView.findViewById(R.id.tv_name)
        var ivAvatar = itemView.findViewById<AppCompatImageView>(R.id.iv_avatar)
        var tvRole = itemView.findViewById<AppCompatTextView>(R.id.tv_role)
        var cbRatingBar = itemView.findViewById<CBRatingBar>(R.id.rating_bar)
        var checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)


    }
}

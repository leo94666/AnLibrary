package com.android.xg.ambulance.export.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.xg.ambulance.export.R
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean
import com.top.arch.util.TimeUtils
import java.util.*


class MeetAdapter(
    private val mContext: Context
) :
    RecyclerView.Adapter<MeetAdapter.RecordHolder?>() {

    private lateinit var mData: MutableList<MeetHistoryResultBean.MeetHistoryBean>

    private var mOnSelectedListener: OnSelectedListener? = null

    interface OnSelectedListener {
        fun onSelectedItem(
            position: MeetHistoryResultBean.MeetHistoryBean
        )
    }

    fun setOnSelectedListener(mOnSelectedListener: OnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener
    }


    init {
        this.mData = mutableListOf()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_meet, parent, false)
        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.tvTitle.text = mData[position].title
        holder.tvTimes.text = TimeUtils.date2String(Date(mData[position].createTime!!))
        when (mData[position].status) {
            0 -> {
                holder.tvStatus.text = "未开始"
                holder.tvEnter.text = ""
            }
            1 -> {
                holder.tvStatus.text = "进行中"
            }
            2 -> {
                holder.tvStatus.text = "已结束";
                holder.tvEnter.text = ""
            }
            else -> {
                holder.tvStatus.text = "未知状态"
            }
        }

        holder.itemView.setOnClickListener {
            mOnSelectedListener?.onSelectedItem(mData[position]);
        }
    }

    override fun getItemCount(): Int {
        return if (mData.isNullOrEmpty()) {
            0
        } else {
            mData.size
        }
    }

    public fun updateData(mData: MutableList<MeetHistoryResultBean.MeetHistoryBean>) {
        if (this.mData.isNullOrEmpty()) {
            this.mData = mutableListOf()
        }
        this.mData.clear()
        this.mData.addAll(mData)
        notifyDataSetChanged()
    }

    public fun addData(mData: MutableList<MeetHistoryResultBean.MeetHistoryBean>) {
        if (this.mData.isNullOrEmpty()) {
            this.mData = mutableListOf()
        }
        this.mData.addAll(mData)
        notifyDataSetChanged()
    }

    class RecordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTitle: AppCompatTextView = itemView.findViewById(R.id.tv_title)
        var tvTimes: AppCompatTextView = itemView.findViewById(R.id.tv_time)
        var tvStatus: AppCompatTextView = itemView.findViewById(R.id.tv_status)
        var tvEnter: AppCompatTextView = itemView.findViewById(R.id.tv_enter)

    }
}

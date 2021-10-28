package com.android.xg.ambulance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.xg.ambulance.export.Exports
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean
import com.top.arch.util.TimeUtils
import java.util.*


class RecordAdapter(
    private val mContext: Context
) :
    RecyclerView.Adapter<RecordAdapter.RecordHolder?>() {

    private lateinit var mData: MutableList<MeetHistoryResultBean.MeetHistoryBean>

    private var mOnSelectedListener: OnSelectedListener? = null

    interface OnSelectedListener {
        fun onSelectedItem(position: Exports, isChecked: Boolean): Boolean
    }

    fun setOnSelectedListener(mOnSelectedListener: OnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener
    }


    init {
        this.mData = mutableListOf()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false)
        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.tvItemTitle.text = mData[position].title
        holder.tvItemStartTime.text = TimeUtils.date2String(Date(mData[position].createTime!!))
        holder.tvItemStatus.text = if (mData[position].status == 0) {
            "未开始"
        } else if (mData[position].status == 1) {
            "未开始"
        } else if (mData[position].status == 2) {
            "已结束"
        } else {
            "未知状态"
        }
    }

    override fun getItemCount(): Int {
        return if (mData.isNullOrEmpty()) {
            0
        } else {
            mData.size
        }
    }

    public fun addData(mData: MutableList<MeetHistoryResultBean.MeetHistoryBean>) {
        if (this.mData.isNullOrEmpty()) {
            this.mData = mutableListOf()
        }
        this.mData.addAll(mData)
        notifyDataSetChanged()

    }

    class RecordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItemTitle: AppCompatTextView = itemView.findViewById(R.id.tv_item_title)
        var tvItemStartTime: AppCompatTextView = itemView.findViewById(R.id.tv_item_start_time)
        var tvItemStatus: AppCompatTextView = itemView.findViewById(R.id.tv_item_status)


    }
}

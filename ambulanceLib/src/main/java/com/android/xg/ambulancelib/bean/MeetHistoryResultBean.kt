package com.android.xg.ambulancelib.bean


data class MeetHistoryResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: ArrayList<MeetHistoryBean>?
) {
    data class MeetHistoryBean(
        var meetingId: String?,  //会议id
        var title: String?,     //会议标题
        var initiatorId: String?, //发起端id
        var roomNo: String?,  //房间号
        var password: String?,  //密码
        var status: Int?,    //状态
        var screenNum:Int?,//屏幕数量
        var source:String?, //创建来源
        var startTime: Long?, //开始时间
        var endTime: Long?,  //结束时间
        var createTime: Long? //创建时间
    )
}
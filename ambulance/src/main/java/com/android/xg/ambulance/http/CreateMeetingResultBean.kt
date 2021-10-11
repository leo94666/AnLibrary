package com.android.xg.ambulance.http

data class CreateMeetingResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: CreateMeetingBean
) {
    data class CreateMeetingBean(
        var meetingId: String?,  //会议id
        var title: String?,     //会议标题
        var initiatorId: String?, //发起端id
        var roomNo: String?,  //房间号
        var password: String?,  //密码
        var status: String?,    //状态
        var startTime: String?, //开始时间
        var endTime: String?,  //结束时间
        var createTime: String? //创建时间
    )
}
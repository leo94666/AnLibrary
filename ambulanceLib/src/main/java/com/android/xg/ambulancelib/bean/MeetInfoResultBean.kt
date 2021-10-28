package com.android.xg.ambulancelib.bean

import java.math.BigInteger


data class MeetInfoResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: MeetInfoBean?
) {
    data class MeetInfoBean(
        var meetingId: String?,  //会议id
        var title: String?,     //会议标题
        var roomNo: String?,  //房间号
        var password: String?,  //密码
        var status: Int?,    //状态
        var source: String?, //创建来源
        var screenNum: Int?,//屏幕数量
        var initiator: Initiator?, //发起端id
        var members: ArrayList<Member>,
        var startTime: Long?, //开始时间
        var endTime: Long?,  //结束时间
        var createTime: Long? //创建时间
    )

    data class Initiator(
        var initiatorId: String,
        var name: String,
        var userId: String,
        var userName: String,
        var licenseNumber: String,
        var type: Int,
        var hospital: UserResultBean.UserInfo.Hospital,
        var createTime: BigInteger,

        )

    data class Member(
        var meetingId: String?,  //会议id
        var userId: String?,
        var userName: String?,
        var info: Info?,
        var hasJoined: Boolean?,
        var joinTime: String?,
        var exitTime: String?,
        var online: Boolean?,
        var createTime: BigInteger?,
    )

    data class Info(
        var meetingId: String?,
        var email: String?,
        var mobile: String?,
        var sex: String?,
        var avatar: String?,
        var sign: String?,
    )
}
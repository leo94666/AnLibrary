package com.android.xg.ambulancelib.bean

data class UserResultBean(var statusCode: String?, var statusMessage: String?, var data: UserInfo?) {
    data class UserInfo(
        var initiatorId: Int?,
        var name: String?,
        var licenseNumber:String?,
        var userId: String?,
        var hospital: Hospital?,
        var avatar: String?,
        var createTime: String?
    ){
        data class Hospital(
            var hospitalId:Int,
            var name:String,
            var createTime: String?
        )
    }
}
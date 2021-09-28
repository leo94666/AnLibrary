package com.android.xg.ambulance.http

import java.math.BigInteger

/**
 * 验证码
 */
data class RankResultBean(var statusCode: String?, var statusMessage: String?, var data: RankBean?)

data class RankBean(
    var rankId: Int?,
    var rankName: String?,
    var screenNum: Int?,
    var createTime: BigInteger?
)
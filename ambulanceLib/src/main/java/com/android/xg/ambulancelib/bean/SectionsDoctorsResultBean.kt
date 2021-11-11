package com.android.xg.ambulancelib.bean

import java.math.BigInteger

class SectionsDoctorsResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: ArrayList<SectionsDoctors>
) {
    class SectionsDoctors(
        var sectionId: Int,
        var name: String,
        var createTime: BigInteger,
        var doctors: ArrayList<DoctorResultBean.Doctor>
    )

}
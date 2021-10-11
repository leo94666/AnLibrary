package com.android.xg.ambulance.http;


import com.android.xg.bean.DeviceResultBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AmbulanceRestService {
    /**
     * @param header
     * @param map    1.账号密码登录
     *               {
     *               "userPhone":"13815456451",
     *               "pwd":"e8dd60281edea8157cce2a887448a21b",
     *               "platform":"pc",
     *               "elabUser":1
     *               }
     *               2.短信登陆
     *               {
     *               "userPhone":"13815456451",
     *               "smsCode":"000000",
     *               "platform":"android",
     *               "elabUser":1
     *               }
     * @return
     */
    @POST("/api/server/account/login")
    Observable<LoginResultBean> loginByAccount(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);

    /**
     * 验证码登陆
     * @param header
     * @param map
     * @return
     */
    @POST("/api/server/account/smslogin")
    Observable<LoginResultBean> loginBySMS(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);

    @POST("/api/server/ambulance/doctor/bind")
    Observable<CaptchaResultBean> getCaptchaCode(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);


    @GET("/api/server/ambulance/user-rank")
    Observable<RankResultBean> rank(@HeaderMap Map<String, String> header);


    @GET("/api/server/ambulance/initiator/bind/doctor")
    Observable<DoctorResultBean> doctor(@HeaderMap Map<String, String> header);




    //会议管理
    @POST("/api/server/ambulance/meeting")
    Observable<DoctorResultBean> createMeeting(@Body Map<String, String> header);

    @PUT("/api/server/ambulance/meeting")
    Observable<DoctorResultBean> modifyMeeting(@Body Map<String, String> header);

    @DELETE("/api/server/ambulance/meeting/{meetingId}")
    Observable<DoctorResultBean> deleteMeeting(@Body Map<String, String> header);

    @DELETE("/api/server/ambulance/meeting/{meetingId}")
    Observable<DoctorResultBean> queryMeeting(@Body Map<String, String> header);

}

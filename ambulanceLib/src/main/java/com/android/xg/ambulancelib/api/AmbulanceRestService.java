package com.android.xg.ambulancelib.api;


import com.android.xg.ambulancelib.bean.ActiveDeviceResultBean;
import com.android.xg.ambulancelib.bean.CaptchaResultBean;
import com.android.xg.ambulancelib.bean.CreateMeetingResultBean;
import com.android.xg.ambulancelib.bean.DoctorResultBean;
import com.android.xg.ambulancelib.bean.LoginResultBean;
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean;
import com.android.xg.ambulancelib.bean.MeetInfoResultBean;
import com.android.xg.ambulancelib.bean.RankResultBean;
import com.android.xg.ambulancelib.bean.ResultBean;
import com.android.xg.ambulancelib.bean.SectionsDoctorsResultBean;
import com.android.xg.ambulancelib.bean.TimProfileResultBean;
import com.android.xg.ambulancelib.bean.UserResultBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    @POST("/api/server/account/register")
    Observable<ResultBean> register(@Body Map<String, Object> map);

    /**
     * 验证码登陆
     * @param header
     * @param map
     * @return
     */
    @POST("/api/server/account/smslogin")
    Observable<LoginResultBean> loginBySMS(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);


    /**
     * 激活设备
     * @param header
     * @param map
     * @return
     */
    @POST("/api/server/ambulance/terminal/actived")
    Observable<ActiveDeviceResultBean> activeDevice(@Body Map<String, Object> map);




    @POST("/api/server/ambulance/doctor/bind")
    Observable<CaptchaResultBean> getCaptchaCode(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);



    @POST("/api/server/tim/account/timprofile")
    Observable<TimProfileResultBean> timProfile(@HeaderMap Map<String, String> header, @Body Map<String, Object> map);


    @GET("/api/server/ambulance/user-rank")
    Observable<RankResultBean> rank(@HeaderMap Map<String, String> header);


    @GET("/api/server/ambulance/initiator/bind/doctor")
    Observable<DoctorResultBean> doctor(@HeaderMap Map<String, String> header);

    @GET("/api/server/ambulance/initiator")
    Observable<UserResultBean> getUserInfo(@HeaderMap Map<String, String> header);


    //会议管理
    @POST("/api/server/ambulance/meeting")
    Observable<CreateMeetingResultBean> createMeeting(@HeaderMap Map<String, String> header, @Body Map<String, Object> body);

    @PUT("/api/server/ambulance/meeting")
    Observable<CreateMeetingResultBean> modifyMeeting(@HeaderMap Map<String, String> header,@Body Map<String, Object> body);

    @DELETE("/api/server/ambulance/meeting/{meetingId}")
    Observable<DoctorResultBean> deleteMeeting(@Body Map<String, String> header);

    @DELETE("/api/server/ambulance/meeting/{meetingId}")
    Observable<DoctorResultBean> queryMeeting(@Body Map<String, String> header);

    @GET("/api/server/ambulance/initiator/meeting/history")
    Observable<MeetHistoryResultBean> getHistoryMeeting(@HeaderMap Map<String, String> header);

    @POST("/api/server/ambulance/initiator/meeting/invite")
    Observable<ResultBean> invite(@HeaderMap Map<String, String> header, @Body Map<String,Object > body);

    @GET("/api/server/ambulance/meeting/{meetingId}")
    Observable<MeetInfoResultBean> meetInfo(@HeaderMap Map<String, String> header, @Path("meetingId") String meetingId);

    @GET("/api/server/ambulance/initiator/meeting/history")
    Observable<MeetHistoryResultBean> historyMeeting(@HeaderMap Map<String, String> header);


    //专家端
    @GET("/api/server/ambulance/initiator/meeting/member/{meetingId}")
    Observable<MeetHistoryResultBean> roomMembers(@HeaderMap Map<String, String> header, @Path("meetingId") String meetingId);

    @GET("/api/server/ambulance/doctor/meeting/join/{meetingId}")
    Observable<MeetHistoryResultBean> joinMeeting(@HeaderMap Map<String, String> header, @Path("meetingId") String meetingId);

    @GET("/api/server/ambulance/doctor/meeting/exit/{meetingId}")
    Observable<MeetHistoryResultBean> exitMeeting(@HeaderMap Map<String, String> header, @Path("meetingId") String meetingId);

    @GET("/api/server/ambulance/doctor/meeting/alive")
    Observable<MeetHistoryResultBean> aliveMeeting(@HeaderMap Map<String, String> header);




    //
    @DELETE("/api/server/ambulance/ident/{ident}")
    Observable<ResultBean> removeID(@HeaderMap Map<String, String> header,@Path("ident") String id);



    //
    @GET("/api/server/ambulance/hospital/{hospitalId}/sections")
    Observable<ResultBean> sections(@HeaderMap Map<String, String> header,@Path("hospitalId") String hospital);

    @GET("/api/server/ambulance/hospital/{hospitalId}/sections/doctors")
    Observable<SectionsDoctorsResultBean> sectionsDoctors(@HeaderMap Map<String, String> header, @Path("hospitalId") int hospital);
}

package com.android.xg.ambulance.personal;

import android.os.Environment;
import android.util.Log;

import com.android.xg.ambulance.http.RankBean;
import com.android.xg.ambulance.http.SecretBean;
import com.elab.libarch.utils.DateUtils;
import com.elab.libarch.utils.FileUtils;
import com.google.gson.Gson;
import com.top.arch.util.SPUtils;
import com.top.arch.util.TimeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Date;


/**
 * ELab个人信息管理
 */
public class AmbulanceProfileManager {

    private static final String TAG = "AmbulanceProfileManager";
    private final static String PER_DATA = "per_profile_manager";
    private static final String PER_USER_ID = "per_user_id";
    private static final String PER_USER_CAR_NUMBER = "per_user_car_NUMBER";
    private static final String PER_USER_LOGIN_INFO = "per_user_login_info";
    private static final String PER_USER_RANK = "per_user_rank";


    private static final String SAVE_ROOT_PATH = "/Ambulance/";
    private static String SAVE_RECORD_VIDEO_PATH;


    private Gson gson;
    private static final AmbulanceProfileManager ourInstance = new AmbulanceProfileManager();

    public static AmbulanceProfileManager getInstance() {
        return ourInstance;
    }

    public AmbulanceProfileManager() {
        if (gson == null) {
            gson = new Gson();
        }
        SAVE_RECORD_VIDEO_PATH = SAVE_ROOT_PATH + getUserId() + "/record/";
    }
    public String getRecordVideoDirPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            Date date = new Date();
            return sdDir.getAbsoluteFile() + SAVE_RECORD_VIDEO_PATH ;
        }
        return "";
    }


    public String getRecordVideoSavePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File sdDir = Environment.getExternalStorageDirectory();//获取跟目录

            Date date = new Date();
            String path = sdDir.getAbsoluteFile() + SAVE_RECORD_VIDEO_PATH + TimeUtils.date2String(date, "yyyy-MM-dd") + File.separator;
            if (FileUtils.isFolderExists(path)) {
                Log.e(TAG, "文件路径存在:" + path);
            } else {
                Log.e(TAG, "文件路径不存在:" + path);
                FileUtils.createOrExistsDir(path);
            }
            return path + "record_" + TimeUtils.date2String(date, "HH_mm_sss") + ".mp4";
        }
        return "";
    }


    public void login(@NotNull String carNumber, @NotNull String account, @Nullable SecretBean data) {
        SPUtils.getInstance(PER_DATA).put(PER_USER_CAR_NUMBER, carNumber);
        SPUtils.getInstance(PER_DATA).put(PER_USER_ID, account);
        SPUtils.getInstance(PER_DATA).put(PER_USER_LOGIN_INFO, gson.toJson(data));
    }

    public boolean isLogin() {
        String userId = SPUtils.getInstance(PER_DATA).getString(PER_USER_ID, "");
        return !userId.equals("");
    }

    public void logout() {
        SPUtils.getInstance(PER_DATA).put(PER_USER_ID, "");
        SPUtils.getInstance(PER_DATA).put(PER_USER_LOGIN_INFO, (String) null);
    }

    public String getCarNumber() {
        String carNumber = SPUtils.getInstance(PER_DATA).getString(PER_USER_CAR_NUMBER);
        return carNumber;
    }

    public String getUserId() {
        String userId = SPUtils.getInstance(PER_DATA).getString(PER_USER_ID);
        return userId;
    }

    public SecretBean getSecretBean() {
        String secret = SPUtils.getInstance(PER_DATA).getString(PER_USER_LOGIN_INFO);
        return gson.fromJson(secret, SecretBean.class);
    }

    public void setRank(@Nullable RankBean data) {
        SPUtils.getInstance(PER_DATA).put(PER_USER_RANK, gson.toJson(data));
    }

    public RankBean getRank() {
        String rank = SPUtils.getInstance(PER_DATA).getString(PER_USER_RANK);
        return gson.fromJson(rank, RankBean.class);
    }

    public int getScreenNum() {
        String rank = SPUtils.getInstance(PER_DATA).getString(PER_USER_RANK);
        return gson.fromJson(rank, RankBean.class).getScreenNum();
    }
}

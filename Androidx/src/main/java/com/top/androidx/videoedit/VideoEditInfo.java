package com.top.androidx.videoedit;

/**
 * @author leo
 * @version 1.0
 * @className VideoEditInfo
 * @description 视频信息
 * @date 2022/2/15 15:36
 **/
public class VideoEditInfo {
    private String path; //图片的sd卡路径
    private long time;//图片所在视频的时间  毫秒

    public VideoEditInfo() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "VideoEditInfo{" +
                "path='" + path + '\'' +
                ", time=" + time +
                '}';
    }
}

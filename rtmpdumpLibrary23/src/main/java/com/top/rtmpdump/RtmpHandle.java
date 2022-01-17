package com.top.rtmpdump;

/**
 * @author leo
 * @version 1.0
 * @className RtmpHandle
 * @description TODO
 * @date 2022/1/17 20:34
 **/
public class RtmpHandle {
    public static RtmpHandle mInstance;

    private RtmpHandle() {
    }

    public synchronized static RtmpHandle getInstance() {
        if (mInstance == null) {
            mInstance = new RtmpHandle();
        }
        return mInstance;
    }

    static {
        System.loadLibrary("rtmp");
    }

    public native void pushFile(String path);
}

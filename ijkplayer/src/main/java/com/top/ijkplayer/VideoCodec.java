package com.top.ijkplayer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCodec {
    private Handler mHandler;
    private MediaMuxer mMuxer;
    private MediaCodec mediaCodec;
    private int videoTrack;
    private boolean isRecording;
    private int width;
    private int height;

    public void startRecoding(String path, int width, int height, int degress) {
        this.width = width;
        this.height = height;
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            //色彩空间  不同设备支持不同，API 21加入420Flexible
            // MediaCodec的所有硬编解码都支持这种格式，但是YUV420的具体格式又会因设备而异
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            //码率  10Mbps
            format.setInteger(MediaFormat.KEY_BIT_RATE, 10240_000);
            //帧率 fps
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
            //关键帧间隔
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);

            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
//          API23可用：  mediaCodec.setCallback(this,mHandler);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
            //混合器 音频+视频 mp4
            mMuxer = new MediaMuxer(path,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mMuxer.setOrientationHint(degress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HandlerThread thread = new HandlerThread("videoCodec");
        thread.start();
        mHandler = new Handler(thread.getLooper());
        isRecording = true;
    }


    public boolean isRecording() {
        return isRecording;
    }

    public void queueEncode(byte[] buffer) {
        if (!isRecording) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //立即得到有效输入缓冲区
                int index = mediaCodec.dequeueInputBuffer(0);
                if (index >= 0) {
                    MediaFormat inputFormat = mediaCodec.getInputFormat();
                    //根据格式来转换输入数据格式：YUV420SP：nv12、nv21 YUV420P:YV12、YU12(I420)
                    byte[] encodeData = getEncodeData(buffer, inputFormat);
                    //byte容器
                    ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
                    inputBuffer.put(encodeData, 0, encodeData.length);
                    //填充数据后再加入队列
                    mediaCodec.queueInputBuffer(index, 0, encodeData.length,
                            System.nanoTime() / 1000, 0);
                }
                while (true) {
                    //获得输出缓冲区 (编码后的数据从输出缓冲区获得)
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
                    //稍后重试
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        //输出格式发生改变  第一次总会调用，所以在这里开启混合器
                        MediaFormat newFormat = mediaCodec.getOutputFormat();
                        videoTrack = mMuxer.addTrack(newFormat);
                        mMuxer.start();
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        //可以忽略
                    } else {
                        //正常则 encoderStatus 获得缓冲区下标
                        ByteBuffer encodedData = mediaCodec.getOutputBuffer(encoderStatus);
                        //如果当前的buffer是配置信息，不管它 不用写出去
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            bufferInfo.size = 0;
                        }
                        if (bufferInfo.size != 0) {
                            //设置从哪里开始读数据(读出来就是编码后的数据)
                            encodedData.position(bufferInfo.offset);
                            //设置能读数据的总长度
                            encodedData.limit(bufferInfo.offset + bufferInfo.size);
                            //写出为mp4
                            mMuxer.writeSampleData(videoTrack, encodedData, bufferInfo);
                        }

                        // 释放这个缓冲区，后续可以存放新的编码后的数据啦
                        mediaCodec.releaseOutputBuffer(encoderStatus, false);
                    }
                }
            }


        });
    }


    public void stopRecording() {
        // 释放
        isRecording = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
                mMuxer.stop();
                mMuxer.release();
                mMuxer = null;
                mHandler.getLooper().quitSafely();
                mHandler = null;
            }
        });
    }

    byte[] encode;

    private byte[] getEncodeData(byte[] buffer, MediaFormat inputFormat) {
        int color = inputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
        if (encode == null) {
            encode = new byte[buffer.length];
        }
        //根据格式来转换输入数据格式：YUV420SP(Semi-Planar)：nv12、nv21 YUV420P:YV12、YU12(I420)
        switch (color) {
            //i420
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                nv21ToI420(buffer, encode);
                break;
            //YV12
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                nv21ToYv12(buffer, encode);
                break;
            //nv12
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                nv21ToNv12(buffer, encode);
                break;
            //nv21
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                System.arraycopy(buffer, 0, encode, 0, buffer.length);
                break;
            //......
        }
        return encode;
    }

    private void nv21ToI420(byte[] src, byte[] dest) {
        //y数据
        System.arraycopy(src, 0, dest, 0, width * height);
        int index = width * height;
        int u = index / 4;
        for (int i = width * height; i < src.length; i += 2) {
            //src:nv21
            //dest:i420
            dest[index] = src[i + 1];
            dest[index + u] = src[i];
            index++;
        }
    }

    private void nv21ToYv12(byte[] src, byte[] dest) {
        //y数据
        System.arraycopy(src, 0, dest, 0, width * height);
        int index = width * height;
        int v = index / 4;
        for (int i = width * height; i < src.length; i += 2) {
            dest[index] = src[i];
            dest[index + v] = src[i + 1];
            index++;
        }
    }

    private void nv21ToNv12(byte[] src, byte[] dest) {
        //y数据
        System.arraycopy(src, 0, dest, 0, width * height);
        int ysize = width * height;
        for (int i = ysize; i < src.length; i += 2) {
            dest[i + 1] = src[i];
            dest[i] = src[i + 1];
        }
    }


}

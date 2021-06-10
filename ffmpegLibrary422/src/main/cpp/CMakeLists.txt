# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
project(ffmpeg)
aux_source_directory(. DIR_FFMPEG_SRCS)

#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")

#设置生成的so动态库最后输出的路径
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../jnilibs/${ANDROID_ABI})
set(FFMPEG_LIB_DIR ${CMAKE_SOURCE_DIR}/../jnilibs/${ANDROID_ABI})


# 用于各种类型声音、图像编解码
add_library(avcodec
        SHARED
        IMPORTED )
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libavcodec.so )

# 用于各种音视频封装格式的生成和解析，读取音视频帧等功能
add_library(avformat
        SHARED
        IMPORTED )
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libavformat.so )

# 包含一些公共的工具函数
add_library(avutil
        SHARED
        IMPORTED )
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libavutil.so )


# 提供了各种音视频过滤器
add_library(avfilter
        SHARED
        IMPORTED )
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libavfilter.so )


# 用于音频重采样，采样格式转换和混合
add_library(swresample
        SHARED
        IMPORTED )
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libswresample.so )
# 用于视频场景比例缩放、色彩映射转换
add_library(swscale
        SHARED
        IMPORTED )
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libswscale.so )

add_library(avdevice
        SHARED
        IMPORTED )
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_LIB_DIR}/libavdevice.so )


add_library(ffmpeg
        SHARED
        fftools/cmdutils.c
        fftools/ffmpeg.c
        fftools/ffmpeg_filter.c
        fftools/ffmpeg_opt.c
        FFmpegkits.c)

# 引用源码
include_directories(../../../../../cSource/ffmpeg-4.0/
        includes)


find_library(  log-lib log)

target_link_libraries(ffmpeg
        ${log-lib}
        avcodec
        avformat
        avutil
        avfilter
        swresample
        swscale )



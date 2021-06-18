#set(pathToOpenCv /Users/woodstream/Documents/dev/projects/android/OpenCV-android-sdk)#设置OpenCv的路径变量
cmake_minimum_required(VERSION 3.4.1)
project(opencv)
#支持-std=gnu++11
#set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#配置加载native依赖
set(OPENCV_NATIVE ../native)
include_directories(${OPENCV_NATIVE}/jni/include)

#动态方式加载
add_library(lib_opencv_java4
        SHARED
        IMPORTED ) #表示创建一个导入库，动态方式

#引入libopencv_java3.so文件
set_target_properties(lib_opencv_java4
        PROPERTIES IMPORTED_LOCATION
        ${OPENCV_NATIVE}/libs/${ANDROID_ABI}/libopencv_java4.so
        )


add_library( # Sets the name of the library.
        OpenCVUtils
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        OpenCVUtils.cpp)
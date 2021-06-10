#!/bin/bash
OS=`uname -a`
echo "==============OS Platform : ${OS}==================="
APPLE='Darwin'
UBUNTU='Ubuntu'

if [[ $OS =~ $UBUNTU ]];then
    echo '======================enter in Ubuntu======================'
    NDK=/home/leo/Android/android-ndk-r21e
    PLATFORM=$NDK/platforms/android-21/arch-arm/
    BUILD_PLATFORM=linux-x86_64

elif [[ $OS =~ $APPLE ]];then
    echo '======================enter in MacOS========================'
    
else
    echo '======================enter in unknown======================'
    exit
fi

FFMPEG_DIR=$1
ANDROID_VERSION=21
TOOLCHAIN_VERSION=4.9

ANDROID_ARMV5_CFLAGS="-march=armv5te"
ANDROID_ARMV7_CFLAGS="-march=armv7-a -mfloat-abi=softfp -mfpu=neon"
ANDROID_ARMV8_CFLAGS="-march=armv8-a"
ANDROID_X86_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32"
ANDROID_X86_64_CFLAGS="-march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel"

function build
{
    # arm armeabi-v7a arm-linux-androideabi arm-linux-androideabi "$ANDROID_ARMV7_CFLAGS"
    echo "-------------------star build $2-------------------------"
    ARCH=$1 #arm arm64 x86 x86_64
    ANDROID_ARCH_ABI=$2 #armeabi armeabi-v7a x86 mips
    PREFIX=./out/android/ffmpeg/${ANDROID_ARCH_ABI}/
    HOST=$3
    SYSROOT=${NDK}/platforms/android-${ANDROID_VERSION}/arch-${ARCH}
    CFALGS=$5
    TOOLCHAIN=${NDK}/toolchains/${HOST}-${TOOLCHAIN_VERSION}/prebuilt/${BUILD_PLATFORM}
    CROSS_PREFIX=${TOOLCHAIN}/bin/$4-
    
    
    #输出配置信息
    echo "pwd==$(pwd)"
    echo "ARCH==${ARCH}"
    echo "PREFIX==${PREFIX}"
    echo "HOST==${HOST}"
    echo "SYSROOT=${SYSROOT}"
    echo "CFALGS=${CFALGS}"
    echo "TOOLCHAIN==${TOOLCHAIN}"
    echo "CROSS_PREFIX=${CROSS_PREFIX}"
    echo "-------------------------按任意键继续---------------------"
    read -n 1
    echo "-------------------------继续执行-------------------------"
    
    cd $FFMPEG_DIR

    sudo ./configure \
            --prefix=$PREFIX \
            --enable-static \
            --enable-pic \
            --host=arm-linux \
            --target-os=android \
            --cross-prefix=${CROSS_PREFIX} \
            --sysroot=$SYSROOT
            --extra-cflags="$CFALGS -Os -fPIC -DANDROID -Wfatal-errors -Wno-deprecated" \
            --extra-cxxflags="-D__thumb__ -fexceptions -frtti" \
            --extra-ldflags="-L${SYSROOT}/usr/lib" \
            --enable-shared \
            --enable-asm \
            --enable-neon \
            --disable-encoders \
            --enable-encoder=aac \
            --enable-encoder=mjpeg \
            --enable-encoder=png \
            --disable-decoders \
            --enable-decoder=aac \
            --enable-decoder=aac_latm \
            --enable-decoder=h264 \
            --enable-decoder=mpeg4 \
            --enable-decoder=mjpeg \
            --enable-decoder=png \
            --disable-demuxers \
            --enable-demuxer=image2 \
            --enable-demuxer=h264 \
            --enable-demuxer=aac \
            --disable-parsers \
            --enable-parser=aac \
            --enable-parser=ac3 \
            --enable-parser=h264 \
            --enable-gpl \
            --disable-doc \
            --disable-ffmpeg \
            --disable-ffplay \
            --disable-ffprobe \
            --disable-symver \
            --disable-debug \
            --enable-small
    
    sudo make clean
    sudo make
    sudo make install
    
    echo "-------------------$2 build end-------------------------"
    cd -

    
}

build arm armeabi-v7a arm-linux-androideabi arm-linux-androideabi "$ANDROID_ARMV7_CFLAGS"



#!/bin/bash
#set -x

OS=`uname -a`
echo "==============OS Platform : ${OS}==================="
APPLE='Darwin'
UBUNTU='Ubuntu'

if [[ $OS =~ $UBUNTU ]];then
    echo '======================enter in Ubuntu======================'
    NDK=/home/leo/Android/android-ndk-r21e     # NDK的路径，根据自己的NDK位置进行设置
    PLATFORM=$NDK/platforms/android-21/arch-arm/
    BUILD_PLATFORM=linux-x86_64

elif [[ $OS =~ $APPLE ]];then
    echo '======================enter in MacOS========================'
    NDK=/Users/liyang/Android/android-ndk-r21e    # NDK的路径，根据自己的NDK位置进行设置
    PLATFORM=$NDK/platforms/android-21/arch-arm/
    BUILD_PLATFORM=darwin-x86_64
else
    echo '======================enter in unknown======================'
    exit
fi

FFMPEG_DIR=$1
ANDROID_VERSION=21 # 目标Android版本
TOOLCHAIN_VERSION=4.9

ANDROID_ARMV5_CFLAGS="-march=armv5te"
ANDROID_ARMV7_CFLAGS="-march=armv7-a -mfloat-abi=softfp -mfpu=neon"
ANDROID_ARMV8_CFLAGS="-march=armv8-a"
ANDROID_X86_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32"
ANDROID_X86_64_CFLAGS="-march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel"

function build
{
    # arm armeabi-v7a arm-linux-androideabi armv7a-linux-androideabi "$ANDROID_ARMV7_CFLAGS"
    echo "-------------------star build $2-------------------------"
    ARCH=$1 #arm arm64 x86 x86_64
    ANDROID_ARCH_ABI=$2 #armeabi armeabi-v7a x86 mips
    PREFIX=$(pwd)/out/android/ffmpeg/${ANDROID_ARCH_ABI}/  #so库输出目录
    CFALGS=$5
    TOOLCHAIN=${NDK}/toolchains/llvm/prebuilt/${BUILD_PLATFORM} #编译工具链目录：
    CROSS_PREFIX=${TOOLCHAIN}/bin/$3- #编译工具链目录：
    CROSS_PREFIX_CLANG=$TOOLCHAIN/bin/$4$ANDROID_VERSION- #编译工具链目录：
    SYSROOT=$TOOLCHAIN/sysroot  #交叉编译环境目录,用于配置交叉编译环境的 根路径 ，编译的时候会默认从这个路径下去寻找 usr/include usr/lib 这两个路径，进而找到相关的头文件和库文件。

    
    
    #输出配置信息
    echo "pwd==$(pwd)."
    echo "ARCH==${ARCH}."
    echo "PREFIX==${PREFIX}."
    echo "SYSROOT=${SYSROOT}."
    echo "CFALGS=${CFALGS}."
    echo "TOOLCHAIN==${TOOLCHAIN}."
    echo "CROSS_PREFIX=${CROSS_PREFIX}."
    echo "CROSS_PREFIX_CLANG=${CROSS_PREFIX_CLANG}."
    echo "-------------------------按任意键继续---------------------"
    read -n 1
    echo "-------------------------继续执行-------------------------"
    
    cd $FFMPEG_DIR

    sudo ./configure \
    --target-os=android \
    --prefix=$PREFIX \
    --arch=$ARCH \
    --arch=arm \
    --cpu=armv7-a \
    --enable-asm \
    --enable-neon \
    --enable-cross-compile \
    --enable-shared \
    --disable-static \
    --disable-doc \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-symver \
    --disable-ffmpeg \
    --cross-prefix=${CROSS_PREFIX} \
    --cross-prefix-clang=${CROSS_PREFIX_CLANG} \
    --sysroot=$SYSROOT  \
    --extra-cflags="-fPIC" \
    --extra-ldflags="-L${SYSROOT}/usr/lib" \
    
    sudo make clean
    sudo make
    sudo make install
    
    echo "-------------------$2 build end-------------------------"
    cd -

    
}


read -p "
Please select !

    1. build for ABI=armeabi-v7a CPU=armv7-a
    2. build for ABI=arm64-v8a CPU=armv8-a(生成不了！)
    3. build for ABI=x86 CPU=x86
    4. build for ABI=x86_64 CPU=x86_64
    5. build all

Please input your choice : " -t 30 choice
case $choice in
	"1")
        build arm armeabi-v7a arm-linux-androideabi armv7a-linux-androideabi
	;;
	"2")
        build arm64 arm64-v8a aarch64-linux-androideabi aarch64-linux-androideabi
	;;
	*)
	    echo "your choice is error!"
	;;
esac



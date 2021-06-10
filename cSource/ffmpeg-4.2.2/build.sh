#!/bin/bash
 
#ffmpeg 版本
FFMPEG_VERSION=4.2.2
#NDK 路径， 根据情况替换
export NDK=/Users/liyang/Android/android-ndk-r21e
#编译输出路径 根据需求替换替换，默认在脚本上级目录ffmpeg_for_android目录下
OUT_DIR="`dirname $PWD`/ffmpeg_for_android/ffmpeg_${FFMPEG_VERSION}_lib"
#编译工具路径，linux版本需根据情况替换
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/darwin-x86_64
 
export TMPDIR=$OUT_DIR/tmp
mkdir -p $TMPDIR
#将ndk下yasm所在路径添加到PATH环境变量
PATH=$PATH:$TOOLCHAIN/bin
#创建/清空编译日志
> $OUT_DIR/build.log
 
function build_start() {
	echo "build FFmpeg for $ABI"
	./configure \
	--prefix=$OUT_DIR/$ABI \
	--disable-gpl \
	--enable-shared \
	--enable-static \
	--disable-doc \
	--disable-programs \
	--disable-avdevice \
	--disable-doc \
	--disable-symver \
	--cross-prefix=$CROSS_PREFIX \
	--target-os=android \
	--enable-cross-compile \
	--arch=$ARCH \
	--cpu=$CPU \
	--cc=$CC \
	--cxx=$CXX \
	--sysroot=$TOOLCHAIN/sysroot \
	--extra-cflags="-Os -fPIC $OPTIMIZE_CFLAGS" \
 
	
	configRet=$?
	echo "* config for $ABI ret:$configRet" >> $OUT_DIR/build.log
	if [ $configRet -eq 0 ]; then
	echo "start make $ABI"
		make clean
		make -j64
		makeRet=$?
		echo "* make for $ABI return:$makeRet" >> $OUT_DIR/build.log
		if [ $makeRet -eq 0 ]; then
			make install
			mv_libs
		fi
	else
		echo "The config of FFmpeg for $ABI is failded"
		echo "* config for $ABI failed !!" >> $OUT_DIR/build.log
	fi
	echo "The Compilation of FFmpeg for $ABI is completed"
	echo "*************************" >> $OUT_DIR/build.log
}
 
function mv_libs() {
	mkdir -p $OUT_DIR/shared-libs/$ABI
	mv $OUT_DIR/$ABI/lib/*.so $OUT_DIR/shared-libs/$ABI
	mkdir -p $OUT_DIR/static-libs/$ABI
	mv $OUT_DIR/$ABI/lib/*.a $OUT_DIR/static-libs/$ABI
	cp -rf $OUT_DIR/$ABI/include $OUT_DIR/
	cp -rf $OUT_DIR/$ABI/share/ffmpeg/examples $OUT_DIR/
	rm -rf $OUT_DIR/$ABI/
}
 
#armv8-a aarch64
function build_arm64() {
	API=21
	ABI=arm64-v8a
	ARCH=arm64
	CPU=armv8-a
	CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
	CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
	CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
	OPTIMIZE_CFLAGS="-march=$CPU"
	echo "* buidl for $ABI start !!" >> $OUT_DIR/build.log
	build_start
	echo "* buidl for $ABI end !!" >> $OUT_DIR/build.log
	echo "*************************" >> $OUT_DIR/build.log
}
 
#armv7-a
function build_arm() {
	API=16
	ABI=armeabi-v7a
	ARCH=arm
	CPU=armv7-a
	CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
	CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
	CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
 
	OPTIMIZE_CFLAGS="-march=$CPU -mfloat-abi=softfp -mfpu=vfp -marm"
	echo "* buidl for $ABI start !!" >> $OUT_DIR/build.log
	build_start
	echo "* buidl for $ABI end !!" >> $OUT_DIR/build.log
	echo "*************************" >> $OUT_DIR/build.log
}
 
#x86 i686
function build_x86() {
	API=16
	ABI=x86
	ARCH=x86
	CPU=x86
	CC=$TOOLCHAIN/bin/i686-linux-android$API-clang
	CXX=$TOOLCHAIN/bin/i686-linux-android$API-clang++
	CROSS_PREFIX=$TOOLCHAIN/bin/i686-linux-android-
	OPTIMIZE_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32 -mno-stackrealign"
	echo "* buidl for $ABI start !!" >> $OUT_DIR/build.log
	build_start
	echo "* buidl for $ABI end !!" >> $OUT_DIR/build.log
	echo "*************************">> $OUT_DIR/build.log
	echo "">> $OUT_DIR/build.log
}
 
#x86_64
function build_x86_64() {
	API=21
	ABI=x86_64
	ARCH=x86_64
	CPU=x86-64
	CC=$TOOLCHAIN/bin/x86_64-linux-android$API-clang
	CXX=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++
	CROSS_PREFIX=$TOOLCHAIN/bin/x86_64-linux-android-
	OPTIMIZE_CFLAGS="-march=$CPU -msse4.2 -mpopcnt -m64 -mtune=intel"
	echo "* buidl for $ABI start !!" >> $OUT_DIR/build.log
	build_start
	echo "* buidl for $ABI end !!" >> $OUT_DIR/build.log
	echo "*************************">> $OUT_DIR/build.log
}
 
function read_opt()
{
	echo ""
	echo "please select onece"
	echo "    1、build for ABI=armeabi-v7a CPU=armv7-a"
	echo "    2、build for ABI=arm64-v8a CPU=armv8-a"
	echo "    3、build for ABI=x86 CPU=x86"
	echo "    4、build for ABI=x86_64 CPU=x86_64"
	echo "    5、build all"
	echo ""
	echo "input:"
	read option
}
 
option=$1
if [ "$option" = "" ];then
	read_opt
fi
 
if [ "$option" = "1" ];then
	echo "start build ABI=arm64-v8a CPU=armv8-a"
	build_arm64
elif [ "$option" = "2" ];then
	echo "start build ABI=armeabi-v7a CPU=armv7-a"
	build_arm
elif [ "$option" = "3" ];then
	echo "start build ABI=x86 CPU=x86"
	build_x86
elif [ "$option" = "4" ];then
	echo "start build ABI=x86_64 CPU=x86_64"
	build_x86_64
elif [ "$option" = "5" ];then
	echo "start build all"
	build_arm64
	build_arm
	build_x86
	build_x86_64
else
	echo "invalid input"
fi
 
rm -rf $TMPDIR
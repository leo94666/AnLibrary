cmake_minimum_required(VERSION 3.4.1)

file(GLOB libs ${SOURCE_DIR}/${FFMPEG_NAME}/lib*)
file(
        COPY ${libs} ${BUILD_DIR}/config.h ${SOURCE_DIR}/${FFMPEG_NAME}/compat
        DESTINATION ${OUT}/include
        FILES_MATCHING PATTERN *.h
)
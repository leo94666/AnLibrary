cmake_minimum_required(VERSION 3.4.1)


file(GLOB libs ${SOURCE_DIR}/${FFMPEG_NAME}/lib*)
message("copy_headers BUILD_DIR============================${BUILD_DIR}")
message("copy_headers OUT============================${OUT}")

message("copy_headers============================${libs}")
file(
        COPY ${libs} ${BUILD_DIR}/config.h ${SOURCE_DIR}/${FFMPEG_NAME}/compat
        DESTINATION ${OUT}/include
        FILES_MATCHING PATTERN *.h
)

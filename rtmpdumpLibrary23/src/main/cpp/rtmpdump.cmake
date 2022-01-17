cmake_minimum_required(VERSION 3.4.1)

# ownload ffmepg source
message("=================================rtmpdump.cmake start===========================")

set(RTMPDUMP_VERSION 2.3)
set(RTMPDUMP_NAME rtmpdump-${RTMPDUMP_VERSION})
set(RTMPDUMP_URL http://rtmpdump.mplayerhq.hu/download/${RTMPDUMP_NAME}.tgz)

message("rtmpdump url : ${RTMPDUMP_URL}")

get_filename_component(RTMPDUMP_ARCHIVE_NAME ${RTMPDUMP_URL} NAME)

message("RTMPDUMP_ARCHIVE_NAME: ${RTMPDUMP_ARCHIVE_NAME}")

IF(NOT EXISTS ${CMAKE_CURRENT_SOURCE_DIR}/${RTMPDUMP_NAME})
    #dwnload source
    file(DOWNLOAD ${RTMPDUMP_URL} ${CMAKE_CURRENT_SOURCE_DIR}/${RTMPDUMP_ARCHIVE_NAME})
    # unzip
    execute_process(
            COMMAND ${CMAKE_COMMAND} -E tar xzf ${CMAKE_CURRENT_SOURCE_DIR}/${RTMPDUMP_ARCHIVE_NAME}
            WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )
ENDIF()


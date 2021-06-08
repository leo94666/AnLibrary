# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

message("CMAKE_SOURCE_DIR = ${CMAKE_SOURCE_DIR}")
message(STATUS "PROJECT_SOURCE_DIR = ${PROJECT_SOURCE_DIR}")
# message(WARNING "CMAKE_BINARY_DIR = ${CMAKE_BINARY_DIR}")
# 添加日志打印出来
message("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA>>>>>>>>>>")
message("当前CMake的路径是：${CMAKE_SOURCE_DIR}")

# Declares and names the project.

project("PlateOCR")
message(STATUS "CMAKE_CURRENT_SOURCE_DIR: ${CMAKE_CURRENT_SOURCE_DIR}")

set(OpenCV_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../sdk/native/jni)

message(STATUS "OpenCV libraries: ${OpenCV_DIR}")

find_package(OpenCV  NAMES OpenCV REQUIRED) #或者用find_package(OpenCV REQUIRED)
if(OpenCV_FOUND)
    message(STATUS "The Opecv lib is found!")
endif()

message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")

aux_source_directory(. SOURCES1)
aux_source_directory(./src SOURCES2)
list (APPEND SOURCES
        ${SOURCES1}
        ${SOURCES2})

message(STATUS "My sources: ${SOURCES}")

add_library( # Sets the name of the library.
        hyperlpr
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${SOURCES})

target_link_libraries(hyperlpr jnigraphics ${OpenCV_LIBS})

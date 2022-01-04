cmake_minimum_required(VERSION 3.4.1)
include_directories(incCn)

add_library(
        hcnetsdk
        SHARED
        hcnetsdk.cpp
)
#target_link_libraries(BBB lib)
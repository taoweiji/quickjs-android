cmake_minimum_required(VERSION 3.10.2)
project(quickjs CXX)
include_directories(quickjs)
set(SOURCE_DIR
        quickjs/cutils.c
        quickjs/libbf.c
        quickjs/libregexp.c
        quickjs/libunicode.c
        quickjs/quickjs.c
        )

file(STRINGS "quickjs/VERSION" CONFIG_VERSION)
add_definitions(-DCONFIG_VERSION="${CONFIG_VERSION}")
add_definitions(-DCONFIG_BIGNUM)
add_definitions(-D_GNU_SOURCE)
add_definitions(-DCONFIG_CC="gcc")
add_definitions(-DCONFIG_PREFIX="/usr/local")
add_library(
        ${PROJECT_NAME}
        SHARED
        ${SOURCE_DIR}
)
target_include_directories(${PROJECT_NAME} PUBLIC .)



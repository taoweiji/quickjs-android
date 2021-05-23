cmake_minimum_required(VERSION 3.10.2)
set(CMAKE_CXX_STANDARD 14)
project(quickjs CXX)
include_directories(quickjs)
#aux_source_directory(quickjs SOURCE_DIR)
#aux_source_directory(repl.c SOURCE_DIR)
set(SOURCE_DIR
        quickjs/cutils.c
        quickjs/libbf.c
        quickjs/libregexp.c
        quickjs/libunicode.c
        quickjs/qjs.c
        quickjs/qjsc.c
        quickjs/quickjs-libc.c
        quickjs/quickjs.c
        quickjs/unicode_gen.c
        )



file(STRINGS "quickjs/VERSION" CONFIG_VERSION)

#set(COMMON_FLAGS -D_GNU_SOURCE -DCONFIG_VERSION=\"${CONFIG_VERSION}\" -DCONFIG_CC=\"${CONFIG_CC}\" -DCONFIG_PREFIX=\"/usr/local\" -DCONFIG_BIGNUM)

add_definitions(-DCONFIG_VERSION="${CONFIG_VERSION}")
add_definitions(-DCONFIG_BIGNUM)
add_definitions(-D_GNU_SOURCE)
add_definitions(-DCONFIG_CC="gcc")
add_definitions(-DCONFIG_PREFIX="/usr/local")
add_definitions(-D__linux__)

add_library(
        ${PROJECT_NAME}
        SHARED
        ${SOURCE_DIR}
        repl.c
        qjscalc.c
)

target_include_directories(${PROJECT_NAME} PUBLIC .)



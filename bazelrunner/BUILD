load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "params",
    visibility = ["//visibility:public"],
    exports = [
        "//bazelrunner/src/main/java/org/jetbrains/bsp/bazel/bazelrunner/params",
    ],
)

java_library(
    name = "bazelrunner",
    visibility = ["//visibility:public"],
    exports = [
        "//bazelrunner:params",
        "//bazelrunner/src/main/java/org/jetbrains/bsp/bazel/bazelrunner",
    ],
)

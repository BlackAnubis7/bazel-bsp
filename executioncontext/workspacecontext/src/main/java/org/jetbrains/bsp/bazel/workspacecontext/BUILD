load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "workspacecontext",
    srcs = glob(["*.kt"]),
    visibility = [
        "//executioncontext/workspacecontext:__pkg__",
        "//executioncontext/workspacecontext/src/test/java/org/jetbrains/bsp/bazel/workspacecontext:__pkg__",
    ],
    exports = [
        "//executioncontext/api",
        "@maven//:io_vavr_vavr",
    ],
    deps = [
        "//executioncontext/api",
        "//executioncontext/projectview:parser",
        "@maven//:io_vavr_vavr",
        "@maven//:org_apache_logging_log4j_log4j_api",
        "@maven//:org_apache_logging_log4j_log4j_core",
    ],
)

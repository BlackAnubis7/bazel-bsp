load("//:junit5.bzl", "kt_junit5_test")

kt_junit5_test(
    name = "ProjectStorageTest",
    size = "small",
    srcs = ["ProjectStorageTest.kt"],
    test_package = "org.jetbrains.bsp.bazel.server.sync",
    deps = [
        "//logger",
        "//server/src/main/java/org/jetbrains/bsp/bazel/server/sync",
        "//utils:dope",
    ],
)

kt_junit5_test(
    name = "JUnit5TestParserTest",
    size = "small",
    srcs = ["JUnit5TestParserTest.kt"],
    test_package = "org.jetbrains.bsp.bazel.server.sync",
    deps = [
        "//server/src/main/java/org/jetbrains/bsp/bazel/server/sync",
    ],
)

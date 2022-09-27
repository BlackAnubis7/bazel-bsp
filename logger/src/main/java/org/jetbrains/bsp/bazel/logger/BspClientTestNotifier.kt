package org.jetbrains.bsp.bazel.logger

import ch.epfl.scala.bsp4j.BuildClient
import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.StatusCode
import ch.epfl.scala.bsp4j.TaskDataKind
import ch.epfl.scala.bsp4j.TaskFinishParams
import ch.epfl.scala.bsp4j.TaskId
import ch.epfl.scala.bsp4j.TaskStartParams
import ch.epfl.scala.bsp4j.TestFinish
import ch.epfl.scala.bsp4j.TestReport
import ch.epfl.scala.bsp4j.TestStart
import ch.epfl.scala.bsp4j.TestStatus
import ch.epfl.scala.bsp4j.TestTask

class BspClientTestNotifier {
  private var bspClient: BuildClient? = null
  private var originId: String? = null

  fun withOriginId(originId: String?): BspClientTestNotifier {
    val bspClientTestNotifier = BspClientTestNotifier()
    bspClientTestNotifier.originId = originId
    bspClientTestNotifier.bspClient = bspClient
    return bspClientTestNotifier
  }

  /**
   * Notifies the client about starting a single test or a test suite
   *
   * @param isSuite     `true` if a test suite has been started, `false` if it was a single test instead
   * @param displayName display name of the started test / test suite
   * @param taskId      TaskId of the started test / test suite
   */
  fun startTest(isSuite: Boolean, displayName: String?, taskId: TaskId?) {
    val testStart = TestStart(displayName)
    val taskStartParams = TaskStartParams(taskId)
    taskStartParams.dataKind = TaskDataKind.TEST_START
    taskStartParams.data = testStart
    if (isSuite) taskStartParams.message = "<S>" else taskStartParams.message = "<T>"
    bspClient!!.onBuildTaskStart(taskStartParams)
  }

  /**
   * Notifies the client about finishing a single test or a test suite
   *
   * @param isSuite     `true` if a test suite has been finished, `false` if it was a single
   * test instead. **For test suites, using `finishTestSuite` is recommended**
   * @param displayName display name of the finished test / test suite
   * @param taskId      TaskId of the finished test / test suite
   * @param status      status of the performed test (does not matter for test suites)
   * @param message     additional message concerning the test execution
   */
  fun finishTest(isSuite: Boolean, displayName: String?, taskId: TaskId?, status: TestStatus?, message: String?) {
    val testFinish = TestFinish(displayName, status)
    testFinish.message = message
    val taskFinishParams = TaskFinishParams(taskId, StatusCode.OK)
    taskFinishParams.dataKind = TaskDataKind.TEST_FINISH
    taskFinishParams.data = testFinish
    if (isSuite) taskFinishParams.message = "<S>" else taskFinishParams.message = "<T>"
    bspClient!!.onBuildTaskFinish(taskFinishParams)
  }

  /**
   * Notifies the client about beginning the testing procedure
   *
   * @param targetIdentifier identifier of the testing target being executed
   * @param taskId           TaskId of the testing target execution
   */
  fun beginTestTarget(targetIdentifier: BuildTargetIdentifier?, taskId: TaskId?) {
    val testingBegin = TestTask(targetIdentifier)
    val taskStartParams = TaskStartParams(taskId)
    taskStartParams.dataKind = TaskDataKind.TEST_TASK
    taskStartParams.data = testingBegin
    bspClient!!.onBuildTaskStart(taskStartParams)
  }

  /**
   * Notifies the client about ending the testing procedure
   *
   * @param testReport report concerning conducted tests
   * @param taskId     TaskId of the testing target execution
   */
  fun endTestTarget(testReport: TestReport?, taskId: TaskId?) {
    val taskFinishParams = TaskFinishParams(taskId, StatusCode.OK)
    taskFinishParams.dataKind = TaskDataKind.TEST_REPORT
    taskFinishParams.data = testReport
    bspClient!!.onBuildTaskFinish(taskFinishParams)
  }

  /**
   * Notifies the client about finishing a test suite. Synonymous to:
   * ```
   * finishTest(true, displayName, taskId, TestStatus.PASSED, "")
   * ```
   *
   * @param displayName display name of the finished test suite
   * @param taskId      TaskId if the finished test suite
   */
  fun finishTestSuite(displayName: String?, taskId: TaskId?) {
    finishTest(true, displayName, taskId, TestStatus.PASSED, "")
  }

  fun initialize(buildClient: BuildClient?) {
    bspClient = buildClient
  }
}
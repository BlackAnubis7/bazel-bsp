package org.jetbrains.bsp.bazel.server.sync
// TODO - rethink the location

import ch.epfl.scala.bsp4j.TaskId
import ch.epfl.scala.bsp4j.TestStatus
import org.jetbrains.bsp.bazel.bazelrunner.BazelProcessResult
import org.jetbrains.bsp.bazel.logger.BspClientTaskNotifier
import java.util.*
import java.util.regex.Pattern

private data class TestSuiteData(
  val displayName: String,
  val taskId: TaskId
)

// TODO - redo everything
class JUnitTestParser(
  private val bspClientTaskNotifier: BspClientTaskNotifier
) {
  fun processTestOutputWithJUnit(testResult: BazelProcessResult) {

    var startedSuite: TestSuiteData? = null
    var startedClass: TestSuiteData? = null

//    notifyProcess(ServiceMessageBuilder("enteredTheMatrix").toString())

    testResult.stdoutLines.forEach {
      val firstLetterPattern = Pattern.compile("\\p{L}")

      val matcherSuite = Pattern.compile("^(?!.*(Junit|Jupiter)).*✔\$").matcher(it).find()
      val matcherSuccessTest = Pattern.compile("\\(\\)\\s✔\$").matcher(it).find()
      val matcherFailTest = Pattern.compile("\\(\\)\\s✘.*").matcher(it).find()

      val testName = it
        .trim()
        .substringAfter("─")
        .substringBefore('✔')
        .substringBefore('✘')
      if (matcherSuite  && !matcherSuccessTest && !matcherFailTest) {
        val firstLetterMatcher = firstLetterPattern.matcher(it)
        if (firstLetterMatcher.find()) {
//          executeCommand("testSuiteFinished","name" to startedSuite)
          if (startedSuite != null) bspClientTaskNotifier.finishTest(
            true,
            startedSuite!!.displayName,
            startedSuite!!.taskId,
            TestStatus.PASSED,
            null
          )
          startedSuite = null
//          val uuid = testUUID()
          val newTaskId = TaskId(testUUID())

          when(firstLetterMatcher.start()) {
            6 -> { // new test class
//              executeCommand("testSuiteFinished","name" to startedClass)
//              executeCommand("testSuiteStarted","name" to result)
              if (startedClass != null) bspClientTaskNotifier.finishTest(
                true,
                startedClass!!.displayName,
                startedClass!!.taskId,
                TestStatus.PASSED,
                null
              )
              bspClientTaskNotifier.startTest(true, testName, newTaskId)
              startedClass = TestSuiteData(testName, newTaskId)
            }
            else -> { // new test suite (e.g. some inner class)
//              executeCommand("testSuiteStarted","name" to result)
              newTaskId.parents = generateParentList(startedClass)
              bspClientTaskNotifier.startTest(true, testName, newTaskId)
              startedSuite = TestSuiteData(testName, newTaskId)
            }
          }
        }
      } else if (matcherSuccessTest || matcherFailTest) {
        val uuid = testUUID()
        val taskId = TaskId(uuid)
        taskId.parents = generateParentList(startedClass, startedSuite)
        bspClientTaskNotifier.startTest(false, testName, taskId)
        if (matcherSuccessTest) {
          bspClientTaskNotifier.finishTest(false, testName, taskId, TestStatus.PASSED, null)
//        executeCommand("testStarted", "name" to result)
//        executeCommand("testFinished", "name" to result)
        }
        else {
          bspClientTaskNotifier.finishTest(
            false,
            testName,
            taskId,
            TestStatus.FAILED,
            it.substringAfter('✘')
          )
//        executeCommand("testStarted", "name" to result)
//        executeCommand("testFailed",
//          "name" to result,
//          "error" to "true",
//          "message" to it.substringAfter('✘'))
        }
      }
    }

//    executeCommand("testSuiteFinished","name" to startedSuite)
//    executeCommand("testSuiteFinished","name" to startedClass)
    if (startedSuite != null) bspClientTaskNotifier.finishTest(
      true,
      startedSuite!!.displayName,
      startedSuite!!.taskId,
      TestStatus.PASSED,
      null
    )
    if (startedClass != null) bspClientTaskNotifier.finishTest(
      true,
      startedClass!!.displayName,
      startedClass!!.taskId,
      TestStatus.PASSED,
      null
    )

//    notifyProcess("Finish\n")
//    processHandler.destroyProcess()
  }

  private fun generateParentList(vararg parents: TestSuiteData?): List<String> =
    generateParentList(parents.toList())

  private fun generateParentList(parents: List<TestSuiteData?>): List<String> =
    parents.filterNotNull().map { it.taskId.id }

  private fun testUUID(): String = "test-" + UUID.randomUUID().toString()
}
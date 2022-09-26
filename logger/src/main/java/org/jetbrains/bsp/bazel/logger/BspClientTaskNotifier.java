package org.jetbrains.bsp.bazel.logger;
// TODO - rethink the location

import ch.epfl.scala.bsp4j.BuildClient;
import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import ch.epfl.scala.bsp4j.StatusCode;
import ch.epfl.scala.bsp4j.TaskDataKind;
import ch.epfl.scala.bsp4j.TaskFinishParams;
import ch.epfl.scala.bsp4j.TaskId;
import ch.epfl.scala.bsp4j.TaskStartParams;
import ch.epfl.scala.bsp4j.TestFinish;
import ch.epfl.scala.bsp4j.TestReport;
import ch.epfl.scala.bsp4j.TestStart;
import ch.epfl.scala.bsp4j.TestStatus;
import ch.epfl.scala.bsp4j.TestTask;

public class BspClientTaskNotifier {
    private BuildClient bspClient;
    private String originId;

    public BspClientTaskNotifier withOriginId(String originId) {
        BspClientTaskNotifier bspClientTaskNotifier = new BspClientTaskNotifier();
        bspClientTaskNotifier.originId = originId;
        bspClientTaskNotifier.bspClient = this.bspClient;
        return bspClientTaskNotifier;
    }

    /**
     * Notifies the client about starting a single test or a test suite
     * @param isSuite <code>true</code> if a test suite has been started, <code>false</code> if it was a single test instead
     * @param displayName display name of the started test / test suite
     * @param taskId TaskId of the started test / test suite
     */
    public void startTest(boolean isSuite, String displayName, TaskId taskId) {
        TestStart testStart = new TestStart(displayName);
        TaskStartParams taskStartParams = new TaskStartParams(taskId);
        taskStartParams.setDataKind(TaskDataKind.TEST_START);
        taskStartParams.setData(testStart);
        if (isSuite) taskStartParams.setMessage("<S>");
        else taskStartParams.setMessage("<T>");
        bspClient.onBuildTaskStart(taskStartParams);
    }

    /**
     * Notifies the client about finishing a single test or a test suite
     * @param isSuite <code>true</code> if a test suite has been finished, <code>false</code> if it was a single
     *               test instead. <b>For test suites, using <code>finishTestSuite</code> is recommended</b>
     * @param displayName display name of the finished test / test suite
     * @param taskId TaskId of the finished test / test suite
     * @param status status of the performed test (does not matter for test suites)
     * @param message additional message concerning the test execution
     */
    public void finishTest(boolean isSuite, String displayName, TaskId taskId, TestStatus status, String message) {
        TestFinish testFinish = new TestFinish(displayName, status);
        testFinish.setMessage(message);
        TaskFinishParams taskFinishParams = new TaskFinishParams(taskId, StatusCode.OK);
        taskFinishParams.setDataKind(TaskDataKind.TEST_FINISH);
        taskFinishParams.setData(testFinish);
        if (isSuite) taskFinishParams.setMessage("<S>");
        else taskFinishParams.setMessage("<T>");
        bspClient.onBuildTaskFinish(taskFinishParams);
    }

    /**
     * Notifies the client about beginning the testing procedure
     * @param targetIdentifier identifier of the testing target being executed
     * @param taskId TaskId of the testing target execution
     */
    public void beginTestTarget(BuildTargetIdentifier targetIdentifier, TaskId taskId) {
        TestTask testingBegin = new TestTask(targetIdentifier);
        TaskStartParams taskStartParams = new TaskStartParams(taskId);
        taskStartParams.setDataKind(TaskDataKind.TEST_TASK);
        taskStartParams.setData(testingBegin);
        bspClient.onBuildTaskStart(taskStartParams);
    }

    /**
     * Notifies the client about ending the testing procedure
     * @param testReport report concerning conducted tests
     * @param taskId TaskId of the testing target execution
     */
    public void endTestTarget(TestReport testReport, TaskId taskId) {
        TaskFinishParams taskFinishParams = new TaskFinishParams(taskId, StatusCode.OK);
        taskFinishParams.setDataKind(TaskDataKind.TEST_REPORT);
        taskFinishParams.setData(testReport);
        bspClient.onBuildTaskFinish(taskFinishParams);
    }

    /**
     * Notifies the client about finishing a test suite. Synonymous to:
     * <pre>finishTest(true, displayName, taskId, TestStatus.PASSED, "")</pre>
     * @param displayName display name of the finished test suite
     * @param taskId TaskId if the finished test suite
     */
    public void finishTestSuite(String displayName, TaskId taskId) {
        finishTest(true, displayName, taskId, TestStatus.PASSED, "");
    }

    public void initialize(BuildClient buildClient) {
        this.bspClient = buildClient;
    }
}

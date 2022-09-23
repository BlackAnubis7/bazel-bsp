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

    public void startTest(boolean isSuite, String displayName, TaskId taskId) {
        TestStart testStart = new TestStart(displayName);
        TaskStartParams taskStartParams = new TaskStartParams(taskId);
        taskStartParams.setDataKind(TaskDataKind.TEST_START);
        taskStartParams.setData(testStart);
        if (isSuite) taskStartParams.setMessage("<S>");
        else taskStartParams.setMessage("<T>");
        bspClient.onBuildTaskStart(taskStartParams);
    }

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

    public void beginTestTarget(BuildTargetIdentifier targetIdentifier, TaskId taskId) {
        TestTask testingBegin = new TestTask(targetIdentifier);
        TaskStartParams taskStartParams = new TaskStartParams(taskId);
        taskStartParams.setDataKind(TaskDataKind.TEST_TASK);
        taskStartParams.setData(testingBegin);
        bspClient.onBuildTaskStart(taskStartParams);
    }

    public void endTestTarget(TestReport testReport, TaskId taskId) {
        TaskFinishParams taskFinishParams = new TaskFinishParams(taskId, StatusCode.OK);
        taskFinishParams.setDataKind(TaskDataKind.TEST_REPORT);
        taskFinishParams.setData(testReport);
        bspClient.onBuildTaskFinish(taskFinishParams);
    }

    public void finishTestSuite(String displayName, TaskId taskId) {
        finishTest(true, displayName, taskId, TestStatus.PASSED, "");
    }

    public void initialize(BuildClient buildClient) {
        this.bspClient = buildClient;
    }
}

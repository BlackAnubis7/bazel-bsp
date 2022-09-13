package org.jetbrains.bsp.bazel.logger;
// TODO - rethink the location

import ch.epfl.scala.bsp4j.BuildClient;
import ch.epfl.scala.bsp4j.StatusCode;
import ch.epfl.scala.bsp4j.TaskDataKind;
import ch.epfl.scala.bsp4j.TaskFinishParams;
import ch.epfl.scala.bsp4j.TaskId;
import ch.epfl.scala.bsp4j.TaskStartParams;
import ch.epfl.scala.bsp4j.TestFinish;
import ch.epfl.scala.bsp4j.TestStart;
import ch.epfl.scala.bsp4j.TestStatus;

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

    public void initialize(BuildClient buildClient) {
        this.bspClient = buildClient;
    }
}

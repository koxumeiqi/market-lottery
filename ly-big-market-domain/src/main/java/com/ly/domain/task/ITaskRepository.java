package com.ly.domain.task;

import com.ly.domain.task.model.entity.TaskEntity;

import java.util.List;

public interface ITaskRepository {
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageSuccess(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);

}

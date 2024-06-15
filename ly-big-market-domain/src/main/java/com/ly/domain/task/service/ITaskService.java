package com.ly.domain.task.service;

import com.ly.domain.task.model.entity.TaskEntity;

import java.util.List;

public interface ITaskService {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskMessageSuccess(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);

}

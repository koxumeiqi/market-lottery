package com.ly.trigger.job;


import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.ly.domain.task.model.entity.TaskEntity;
import com.ly.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class SendMessageTaskJob {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private IDBRouterStrategy dbRouterStrategy;

    @Resource
    private ITaskService taskService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            // 获取到数据源库的数量
            int dbCount = dbRouterStrategy.dbCount();
            // 逐个去遍历扫描对应表【每个分库一个任务表】
            for (int dbIdx = 1; dbIdx <= dbCount; dbIdx++) {
                int finalDbIdx = dbIdx;
                try {
                    dbRouterStrategy.setDBKey(finalDbIdx);
                    List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                    if (taskEntities.isEmpty()) continue;

                    // 发送消息，发货
                    for (TaskEntity taskEntity : taskEntities) {
                        threadPoolExecutor.execute(() -> {
                            try {
                                taskService.sendMessage(taskEntity);
                                taskService.updateTaskMessageSuccess(taskEntity.getUserId(), taskEntity.getMessageId());
                            } catch (Exception e) {
                                log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                            }
                        });
                    }
                } finally {
                    dbRouterStrategy.clear();
                }
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouterStrategy.clear();
        }
    }

}

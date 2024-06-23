package com.ly.api;

import com.ly.api.dto.ActivityDrawRequestDTO;
import com.ly.api.dto.ActivityDrawResponseDTO;
import com.ly.types.model.Response;


/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动服务
 * @create 2024-04-13 09:16
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId 活动ID
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 判断是否完成日历签到返利接口
     *
     * @param userId
     * @return 签到结果 true 表示已签到 false 未签到
     */
    Response<Boolean> isCalendarSignRebate(String userId);

}

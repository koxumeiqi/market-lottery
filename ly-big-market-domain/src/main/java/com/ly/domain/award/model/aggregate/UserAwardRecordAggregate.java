package com.ly.domain.award.model.aggregate;


import com.ly.domain.award.model.entity.TaskEntity;
import com.ly.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecordAggregate {

    private TaskEntity taskEntity;

    private UserAwardRecordEntity userAwardRecordEntity;

}

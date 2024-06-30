package com.ly.domain.award.repository;

import com.ly.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.ly.domain.award.model.aggregate.UserAwardRecordAggregate;

public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    String queryAwardConfig(Integer awardId);

    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

    String queryAwardKey(Integer awardId);

}

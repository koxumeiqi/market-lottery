package com.ly.test.infrastructure;


import com.ly.domain.strategy.model.valobj.RuleTreeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RepositoryTest {

    @Autowired
    private IStrategyRepository repository;

    @Test
    public void testQueryRuleTreeVoByTreeId() {
        RuleTreeVO treeLock = repository.queryRuleTreeVOByTreeId("tree_lock_1");
        log.info("抽奖后规则过滤的详细信息->{}", treeLock);
    }

}

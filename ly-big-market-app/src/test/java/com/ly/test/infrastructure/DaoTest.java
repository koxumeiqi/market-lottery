package com.ly.test.infrastructure;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.infrastructure.persistent.dao.IAwardDao;
import com.ly.infrastructure.persistent.po.Award;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {

    @Resource
    private IAwardDao awardDao;

    @Test
    public void test() {
        List<Award> awards = awardDao.selectList(null);
        log.warn("awards--->{}", awards);
    }

}

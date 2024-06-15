package com.ly.json;

import com.alibaba.fastjson2.JSONObject;
import com.ly.api.dto.RaffleResponseDTO;
import com.ly.types.model.Response;
import org.junit.Test;

public class JsonStringGetTest {

    @Test
    public void testJson() {
        RaffleResponseDTO raffleResponseDTO = new RaffleResponseDTO();
        Response<Object> resp = Response.builder()
                .info("系统繁忙请稍后重试")
                .code("500")
                .data(raffleResponseDTO)
                .build();
        String s = JSONObject.toJSONString(resp);
        System.out.println(s);
    }

}

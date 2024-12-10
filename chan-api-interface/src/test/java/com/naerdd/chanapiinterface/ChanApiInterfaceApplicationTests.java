package com.naerdd.chanapiinterface;

import com.naer.chanapiclientsdk.client.NaerApiClient;
import com.naer.chanapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ChanApiInterfaceApplicationTests {

    @Resource
    private NaerApiClient naerApiClient;

    @Test
    void contextLoads() {
        String result = naerApiClient.getNameByGet("naer");
        User user = new User();
        user.setUsername("naer");
        String naer1 = naerApiClient.getUserNameByPost(user);
        System.out.println(result);
        System.out.println(naer1);
    }

}

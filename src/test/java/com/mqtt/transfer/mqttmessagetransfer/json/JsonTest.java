/**
 * @Author kkf7688
 * @Data 2018/11/22
 * @Version 1.0
 */

package com.mqtt.transfer.mqttmessagetransfer.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonTest {

    @Test
    public void testJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firmName", "kangzhenyu");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject);

    }

}

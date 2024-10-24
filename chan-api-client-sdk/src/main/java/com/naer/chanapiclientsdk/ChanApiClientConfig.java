package com.naer.chanapiclientsdk;

import com.naer.chanapiclientsdk.client.NaerApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("naer.client")
@Data
@ComponentScan
public class ChanApiClientConfig {

    private  String accessKey;

    private  String secretKey;

    public NaerApiClient naerApiClient(){
        return new NaerApiClient(accessKey,secretKey);
    }
}

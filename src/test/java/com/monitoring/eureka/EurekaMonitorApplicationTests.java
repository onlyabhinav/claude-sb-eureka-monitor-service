package com.monitoring.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "monitoring.eureka.registries[0].name=test-eureka",
    "monitoring.eureka.registries[0].url=http://localhost:8761/eureka",
    "monitoring.eureka.registries[0].basicAuth.enabled=false"
})
class EurekaMonitorApplicationTests {

    @Test
    void contextLoads() {
    }
}
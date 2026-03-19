package com.mandeep.blogify.integrationTest.base;

import com.mandeep.blogify.integrationTest.config.TestContainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

}

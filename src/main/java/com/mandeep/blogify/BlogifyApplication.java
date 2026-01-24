package com.mandeep.blogify;

import com.mandeep.blogify.auth.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class BlogifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogifyApplication.class, args);
    }

}

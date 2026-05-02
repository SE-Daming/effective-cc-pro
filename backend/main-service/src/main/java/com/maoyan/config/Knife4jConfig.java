package com.maoyan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 *
 * @author maoyan
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("猫眼电影票务平台 API")
                        .description("猫眼电影票务平台后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("maoyan")
                                .email("maoyan@example.com")));
    }

}

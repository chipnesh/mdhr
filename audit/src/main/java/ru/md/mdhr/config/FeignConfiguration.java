package ru.md.mdhr.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.md.mdhr")
public class FeignConfiguration {

}

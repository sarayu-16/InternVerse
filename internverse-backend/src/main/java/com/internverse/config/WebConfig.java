package com.internverse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${internverse.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dir = Paths.get(uploadDir, "certificates").toAbsolutePath().toString().replace("\\", "/");
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        registry.addResourceHandler("/api/files/certificates/**")
                .addResourceLocations("file:" + dir);
    }
}

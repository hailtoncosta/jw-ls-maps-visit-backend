package com.visit.jw_ls_maps_visit.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration 
public class UploadConfig implements  WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}") String dir;
    @Override 
    public void addResourceHandlers(ResourceHandlerRegistry resouce){
        resouce.addResourceHandler("/uploads/**").addResourceLocations(Paths.get(dir).toAbsolutePath().toUri().toString());
        }
    
}

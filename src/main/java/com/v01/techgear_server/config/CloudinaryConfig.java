package com.v01.techgear_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Configuration
public class CloudinaryConfig {


    @Bean
    @Lazy
    Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "didyifgtm",
            "api_key", "184652387491793",
            "api_secret", "9Il5OK75WHhAc440DQwvf2HSk0M",
            "secure", true));
    }
}

package com.anime.shop.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RegisterDTO{
    private String username;
    private String password;
    private String captcha;
}

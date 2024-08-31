package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.model.Image;

import lombok.Data;

@Data
public class ReviewsDTO {
    private Long id;
    private UserDTO user;
    private String comments;
    private double rating;
    private LocalDateTime reviewDate;
    private Image reviewImage;

}

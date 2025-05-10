package com.v01.techgear_server.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.shared.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
package com.batubook.backend.mapper;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.entity.ReviewEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO reviewEntityToReviewDTO(ReviewEntity reviewEntity);

    ReviewEntity reviewDTOToReviewEntity(ReviewDTO reviewDTO);
}
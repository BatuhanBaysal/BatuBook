package com.batubook.backend.mapper;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { RepostSaveMapper.class, LikeMapper.class })
public interface ReviewMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    ReviewDTO reviewEntityToDTO(ReviewEntity reviewEntity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "bookId", target = "book.id")
    ReviewEntity reviewDTOToEntity(ReviewDTO reviewDTO);
}
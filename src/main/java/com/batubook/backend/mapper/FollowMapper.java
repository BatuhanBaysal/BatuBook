package com.batubook.backend.mapper;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "followedUserId", source = "followedUser.id")
    @Mapping(target = "followedBookId", source = "followedBook.id")
    FollowDTO followEntityToFollowDTO(FollowEntity followEntity);

    @Mapping(target = "follower", source = "followerId")
    @Mapping(target = "followedUser", source = "followedUserId")
    @Mapping(target = "followedBook", source = "followedBookId")
    FollowEntity followDTOToFollowEntity(FollowDTO followDTO);

    default UserEntity map(Long value) {
        if (value == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(value);
        return userEntity;
    }

    default BookEntity mapBook(Long value) {
        if (value == null) {
            return null;
        }

        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(value);
        return bookEntity;
    }
}
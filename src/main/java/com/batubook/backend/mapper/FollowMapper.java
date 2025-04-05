package com.batubook.backend.mapper;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "followedUserId", source = "followedUser.id")
    @Mapping(target = "followedBookId", source = "followedBook.id")
    FollowDTO followEntityToDTO(FollowEntity followEntity);

    @Mapping(target = "follower", source = "followerId", qualifiedByName = "mapUser")
    @Mapping(target = "followedUser", source = "followedUserId", qualifiedByName = "mapUser")
    @Mapping(target = "followedBook", source = "followedBookId", qualifiedByName = "mapBook")
    FollowEntity followDTOToEntity(FollowDTO followDTO);

    @Named("mapUser")
    default UserEntity mapUser(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    @Named("mapBook")
    default BookEntity mapBook(Long id) {
        if (id == null) return null;
        BookEntity book = new BookEntity();
        book.setId(id);
        return book;
    }
}
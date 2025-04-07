package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.FollowMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.FollowRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.FollowServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowServiceInterface {

    private final FollowRepository followRepository;
    private final FollowMapper followMapper;
    private final Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public FollowDTO followUser(FollowDTO followDTO) {
        try {
            UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                    .orElseThrow(() -> new RuntimeException("Follower not found"));
            UserEntity followedUser = userRepository.findById(followDTO.getFollowedUserId())
                    .orElseThrow(() -> new RuntimeException("Followed user not found"));

            if (followRepository.findByFollowerAndFollowedUser(follower, followedUser) != null) {
                throw new RuntimeException("User already follows this user.");
            }

            FollowEntity followEntity = followMapper.followDTOToEntity(followDTO);
            followEntity.setFollower(follower);
            followEntity.setFollowedUser(followedUser);

            followRepository.save(followEntity);
            logger.info("User with ID: {} followed user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId());
            return followMapper.followEntityToDTO(followEntity);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error for follow user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating follow user: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Follow User could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public FollowDTO followBook(FollowDTO followDTO) {
        try {
            UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                    .orElseThrow(() -> new RuntimeException("Follower not found"));
            BookEntity followedBook = bookRepository.findById(followDTO.getFollowedBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            if (followRepository.findByFollowerAndFollowedBook(follower, followedBook) != null) {
                throw new RuntimeException("User already follows this book.");
            }

            FollowEntity followEntity = followMapper.followDTOToEntity(followDTO);
            followEntity.setFollower(follower);
            followEntity.setFollowedBook(followedBook);

            followRepository.save(followEntity);
            logger.info("User with ID: {} followed book with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedBookId());
            return followMapper.followEntityToDTO(followEntity);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error for follow book: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating follow book: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Follow Book could not be created: " + e.getMessage());
        }
    }

    @Override
    public Page<FollowDTO> getAllFollows(Pageable pageable) {
        logger.info("Fetching all follow records. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<FollowEntity> followEntities = followRepository.findAll(pageable);
        logger.info("Fetched {} follow records.", followEntities.getTotalElements());
        return followEntities.map(followMapper::followEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowedUsers(Long followerId, Pageable pageable) {
        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found with ID: " + followerId));

        logger.info("Fetching followed users for follower with ID: {}", followerId);
        Page<FollowEntity> entities = followRepository.findByFollower(follower, pageable);
        logger.info("Fetched {} followed users for follower with ID: {}", entities.getTotalElements(), followerId);
        return entities.map(followMapper::followEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowers(Long followedUserId, Pageable pageable) {
        UserEntity followedUser = userRepository.findById(followedUserId)
                .orElseThrow(() -> new RuntimeException("Followed user not found with ID: " + followedUserId));

        logger.info("Fetching followers for user with ID: {}", followedUserId);
        Page<FollowEntity> entities = followRepository.findByFollowedUser(followedUser, pageable);
        logger.info("Fetched {} followers for user with ID: {}", entities.getTotalElements(), followedUserId);
        return entities.map(followMapper::followEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getBookFollowers(Long followedBookId, Pageable pageable) {
        BookEntity followedBook = bookRepository.findById(followedBookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + followedBookId));

        logger.info("Fetching followers for book with ID: {}", followedBookId);
        Page<FollowEntity> entities = followRepository.findByFollowedBook(followedBook, pageable);
        logger.info("Fetched {} followers for book with ID: {}", entities.getTotalElements(), followedBookId);
        return entities.map(followMapper::followEntityToDTO);
    }

    @Override
    @Transactional
    public void unfollowUser(FollowDTO followDTO) {
        UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                .orElseThrow(() -> new RuntimeException("Follower not found with ID: " + followDTO.getFollowerId()));
        UserEntity followedUser = userRepository.findById(followDTO.getFollowedUserId())
                .orElseThrow(() -> new RuntimeException("Followed user not found with ID: " + followDTO.getFollowedUserId()));

        FollowEntity followEntity = followRepository.findByFollowerAndFollowedUser(follower, followedUser);
        if (followEntity == null) {
            logger.warn("User with ID: {} is not following user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId());
            return;
        }

        followRepository.delete(followEntity);
        logger.info("User with ID: {} unfollowed user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId());
    }

    @Override
    @Transactional
    public void unfollowBook(FollowDTO followDTO) {
        UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                .orElseThrow(() -> new RuntimeException("Follower not found with ID: " + followDTO.getFollowerId()));
        BookEntity followedBook = bookRepository.findById(followDTO.getFollowedBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + followDTO.getFollowedBookId()));

        FollowEntity followEntity = followRepository.findByFollowerAndFollowedBook(follower, followedBook);
        if (followEntity == null) {
            logger.warn("User with ID: {} is not following book with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedBookId());
            return;
        }

        followRepository.delete(followEntity);
        logger.info("User with ID: {} unfollowed book with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedBookId());
    }
}
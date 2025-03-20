package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
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
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

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

            FollowEntity followEntity = new FollowEntity();
            followEntity.setFollower(follower);
            followEntity.setFollowedUser(followedUser);
            followRepository.save(followEntity);
            logger.info("User with ID: {} followed user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId());
            return followMapper.followEntityToFollowDTO(followEntity);

        } catch (Exception e) {
            logger.error("Error following user with ID: {} by user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId(), e);
            throw e;
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

            FollowEntity followEntity = new FollowEntity();
            followEntity.setFollower(follower);
            followEntity.setFollowedBook(followedBook);
            followRepository.save(followEntity);
            logger.info("User with ID: {} followed book with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedBookId());
            return followMapper.followEntityToFollowDTO(followEntity);

        } catch (Exception e) {
            logger.error("Error following book with ID: {} by user with ID: {}", followDTO.getFollowedBookId(), followDTO.getFollowerId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean unfollowUser(FollowDTO followDTO) {
        try {
            UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                    .orElseThrow(() -> new RuntimeException("Follower not found"));
            UserEntity followedUser = userRepository.findById(followDTO.getFollowedUserId())
                    .orElseThrow(() -> new RuntimeException("Followed user not found"));

            FollowEntity followEntity = followRepository.findByFollowerAndFollowedUser(follower, followedUser);
            if (followEntity != null) {
                followRepository.delete(followEntity);
                logger.info("User with ID: {} unfollowed user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId());
                return true;
            }
            return false;

        } catch (Exception e) {
            logger.error("Error unfollowing user with ID: {} by user with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedUserId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean unfollowBook(FollowDTO followDTO) {
        try {
            UserEntity follower = userRepository.findById(followDTO.getFollowerId())
                    .orElseThrow(() -> new RuntimeException("Follower not found"));
            BookEntity followedBook = bookRepository.findById(followDTO.getFollowedBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            FollowEntity followEntity = followRepository.findByFollowerAndFollowedBook(follower, followedBook);
            if (followEntity != null) {
                followRepository.delete(followEntity);
                logger.info("User with ID: {} unfollowed book with ID: {}", followDTO.getFollowerId(), followDTO.getFollowedBookId());
                return true;
            }
            return false;

        } catch (Exception e) {
            logger.error("Error unfollowing book with ID: {} by user with ID: {}", followDTO.getFollowedBookId(), followDTO.getFollowerId(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowedUsers(Long followerId, Pageable pageable) {
        try {
            UserEntity follower = userRepository.findById(followerId)
                    .orElseThrow(() -> new RuntimeException("Follower not found"));
            Page<FollowDTO> followedUsers = followRepository.findByFollower(follower, pageable)
                    .map(followMapper::followEntityToFollowDTO);
            logger.info("Returning {} followed users for follower with ID: {}", followedUsers.getTotalElements(), followerId);
            return followedUsers;

        } catch (Exception e) {
            logger.error("Error getting followed users for follower with ID: {}", followerId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowers(Long followedUserId, Pageable pageable) {
        try {
            UserEntity followedUser = userRepository.findById(followedUserId)
                    .orElseThrow(() -> new RuntimeException("Followed user not found"));
            Page<FollowDTO> followers = followRepository.findByFollowedUser(followedUser, pageable)
                    .map(followMapper::followEntityToFollowDTO);
            logger.info("Returning {} followers for user with ID: {}", followers.getTotalElements(), followedUserId);
            return followers;

        } catch (Exception e) {
            logger.error("Error getting followers for followed user with ID: {}", followedUserId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getBookFollowers(Long followedBookId, Pageable pageable) {
        try {
            BookEntity followedBook = bookRepository.findById(followedBookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            Page<FollowDTO> bookFollowers = followRepository.findByFollowedBook(followedBook, pageable)
                    .map(followMapper::followEntityToFollowDTO);
            logger.info("Returning {} followers for book with ID: {}", bookFollowers.getTotalElements(), followedBookId);
            return bookFollowers;

        } catch (Exception e) {
            logger.error("Error getting followers for book with ID: {}", followedBookId, e);
            throw e;
        }
    }
}
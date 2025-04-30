package com.batubook.backend.Tests.FollowTests;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.FollowRepository;
import com.batubook.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(FollowControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private UserEntity follower;
    private UserEntity followedUser;
    private BookEntity followedBook;

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for FollowController...");
        followRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();
        initializeMockData();
    }

    @Test
    @Order(1)
    public void testFollowUserSuccessfully() throws Exception {
        FollowDTO followDTO = FollowDTO.builder()
                .followerId(follower.getId())
                .followedUserId(followedUser.getId())
                .build();

        mockMvc.perform(post("/api/follows/follow-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.followerId").value(follower.getId()))
                .andExpect(jsonPath("$.followedUserId").value(followedUser.getId()));
    }

    @Test
    @Order(2)
    public void testFollowUserWithMissingField() throws Exception {
        FollowDTO followDTO = FollowDTO.builder()
                .followerId(null)
                .followedUserId(followedUser.getId())
                .build();

        mockMvc.perform(post("/api/follows/follow-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(3)
    public void testFollowBookSuccessfully() throws Exception {
        FollowDTO followDTO = FollowDTO.builder()
                .followerId(follower.getId())
                .followedBookId(followedBook.getId())
                .build();

        mockMvc.perform(post("/api/follows/follow-book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.followerId").value(follower.getId()))
                .andExpect(jsonPath("$.followedBookId").value(followedBook.getId()));
    }

    @Test
    @Order(4)
    public void testFollowBookWithMissingField() throws Exception {
        FollowDTO followDTO = FollowDTO.builder()
                .followerId(follower.getId())
                .followedBookId(null)
                .build();

        mockMvc.perform(post("/api/follows/follow-book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(5)
    public void testUnfollowSuccessfully() throws Exception {
        FollowEntity follow = FollowEntity.builder()
                .follower(follower)
                .followedUser(followedUser)
                .build();

        FollowEntity savedFollow = followRepository.save(follow);

        FollowDTO followDTO = FollowDTO.builder()
                .followerId(savedFollow.getFollower().getId())
                .followedUserId(savedFollow.getFollowedUser().getId())
                .build();

        mockMvc.perform(delete("/api/follows/unfollow-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    public void testUnfollowNotFound() throws Exception {
        FollowDTO followDTO = FollowDTO.builder()
                .followerId(9999L)
                .followedUserId(9999L)
                .build();

        mockMvc.perform(delete("/api/follows/unfollow-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void testGetAllFollows() throws Exception {
        FollowEntity follow1 = FollowEntity.builder()
                .follower(follower)
                .followedUser(followedUser)
                .build();

        FollowEntity follow2 = FollowEntity.builder()
                .follower(follower)
                .followedBook(followedBook)
                .build();

        followRepository.save(follow1);
        followRepository.save(follow2);

        mockMvc.perform(get("/api/follows")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    private void initializeMockData() {
        follower = new UserEntity();
        follower.setUsername("follower");
        follower.setEmail("follower@test.com");
        follower.setPassword("securePassword!123");
        follower.setRole(Role.USER);

        UserProfileEntity followerProfile = new UserProfileEntity();
        followerProfile.setDateOfBirth(LocalDate.of(1995, 5, 20));
        followerProfile.setBiography("Follower user biography");
        followerProfile.setLocation("Ankara");
        followerProfile.setOccupation("Developer");
        followerProfile.setEducation("Computer Engineering");
        followerProfile.setInterests("Coding, AI");
        followerProfile.setProfileImageUrl("http://image.url/follower.jpg");
        followerProfile.setGender(Gender.MALE);
        followerProfile.setUser(follower);
        follower.setUserProfile(followerProfile);

        followedUser = new UserEntity();
        followedUser.setUsername("followed");
        followedUser.setEmail("followed@test.com");
        followedUser.setPassword("securePassword!456");
        followedUser.setRole(Role.USER);

        UserProfileEntity followedProfile = new UserProfileEntity();
        followedProfile.setDateOfBirth(LocalDate.of(1998, 10, 10));
        followedProfile.setBiography("Followed user biography");
        followedProfile.setLocation("Istanbul");
        followedProfile.setOccupation("Designer");
        followedProfile.setEducation("Art School");
        followedProfile.setInterests("Design, Books");
        followedProfile.setProfileImageUrl("http://image.url/followed.jpg");
        followedProfile.setGender(Gender.FEMALE);
        followedProfile.setUser(followedUser);
        followedUser.setUserProfile(followedProfile);

        follower = userRepository.save(follower);
        followedUser = userRepository.save(followedUser);

        followedBook = new BookEntity();
        followedBook.setTitle("1984");
        followedBook.setAuthor("George Orwell");
        followedBook.setIsbn("1234567890");
        followedBook.setPageCount(352);
        followedBook.setPublishDate(LocalDate.of(1949, 6, 8));
        followedBook.setGenre(Genre.DYSTOPIA);

        followedBook = bookRepository.save(followedBook);
    }
}
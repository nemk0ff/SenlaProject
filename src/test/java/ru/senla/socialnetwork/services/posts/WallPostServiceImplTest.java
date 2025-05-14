package ru.senla.socialnetwork.services.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.users.WallPostDao;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.services.posts.impl.WallPostServiceImpl;

@ExtendWith(MockitoExtension.class)
class WallPostServiceImplTest {

  @Mock
  private WallPostDao wallPostDao;

  @InjectMocks
  private WallPostServiceImpl wallPostService;

  private User testUser;
  private WallPost testPost;
  private WallPostRequestDTO testDto;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .build();

    testPost = WallPost.builder()
        .id(TEST_POST_ID)
        .wallOwner(testUser)
        .body(TEST_BODY)
        .mood(TEST_MOOD)
        .location(TEST_LOCATION)
        .createdAt(ZonedDateTime.now())
        .build();

    testDto = new WallPostRequestDTO(TEST_BODY, TEST_MOOD, TEST_LOCATION);
  }

  @Nested
  class GetByUserTests {
    @Test
    void getByUser_whenPostsExist_thenReturnPosts() {
      when(wallPostDao.findAllByUser(TEST_USER_ID)).thenReturn(List.of(testPost));

      List<WallPost> result = wallPostService.getByUser(TEST_USER_ID);

      assertThat(result).containsExactly(testPost);
      verify(wallPostDao).findAllByUser(TEST_USER_ID);
    }

    @Test
    void getByUser_whenNoPosts_thenReturnEmptyList() {
      when(wallPostDao.findAllByUser(TEST_USER_ID)).thenReturn(List.of());

      List<WallPost> result = wallPostService.getByUser(TEST_USER_ID);

      assertThat(result).isEmpty();
      verify(wallPostDao).findAllByUser(TEST_USER_ID);
    }
  }

  @Nested
  class GetTests {
    @Test
    void get_whenPostExists_thenReturnPost() {
      when(wallPostDao.find(TEST_POST_ID)).thenReturn(Optional.of(testPost));

      WallPost result = wallPostService.get(TEST_POST_ID);

      assertThat(result).isEqualTo(testPost);
      verify(wallPostDao).find(TEST_POST_ID);
    }

    @Test
    void get_whenPostNotExists_thenThrowException() {
      when(wallPostDao.find(TEST_POST_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> wallPostService.get(TEST_POST_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("WallPost с id " + TEST_POST_ID + " не найден");
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_whenValidData_thenReturnCreatedPost() {
      when(wallPostDao.saveOrUpdate(any(WallPost.class))).thenReturn(testPost);

      WallPost result = wallPostService.create(testDto, testUser);

      assertThat(result).isEqualTo(testPost);
      assertThat(result.getWallOwner()).isEqualTo(testUser);
      assertThat(result.getBody()).isEqualTo(testDto.body());
      assertThat(result.getMood()).isEqualTo(testDto.mood());
      assertThat(result.getLocation()).isEqualTo(testDto.location());
      verify(wallPostDao).saveOrUpdate(any(WallPost.class));
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_whenValidData_thenReturnUpdatedPost() {
      WallPostRequestDTO updateDto = new WallPostRequestDTO(
          "Updated content",
          "Sad",
          "Saint Petersburg"
      );

      when(wallPostDao.saveOrUpdate(any(WallPost.class))).thenAnswer(invocation -> {
        WallPost post = invocation.getArgument(0);
        post.setBody(updateDto.body());
        post.setMood(updateDto.mood());
        post.setLocation(updateDto.location());
        return post;
      });

      WallPost result = wallPostService.update(testPost, updateDto);

      assertThat(result.getBody()).isEqualTo(updateDto.body());
      assertThat(result.getMood()).isEqualTo(updateDto.mood());
      assertThat(result.getLocation()).isEqualTo(updateDto.location());
      verify(wallPostDao).saveOrUpdate(testPost);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void delete_whenValidPost_thenCallDao() {
      wallPostService.delete(testPost);

      verify(wallPostDao).delete(testPost);
    }
  }
}
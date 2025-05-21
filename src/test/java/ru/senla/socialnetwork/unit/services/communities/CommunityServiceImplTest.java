package ru.senla.socialnetwork.unit.services.communities;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static ru.senla.socialnetwork.unit.TestConstants.TEST_COMMUNITY_ID;
import static ru.senla.socialnetwork.unit.TestConstants.TEST_COMMUNITY_NAME;
import ru.senla.socialnetwork.dao.communities.CommunityDao;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.services.communities.impl.CommunityServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {
  @Mock
  private CommunityDao communityDao;

  @InjectMocks
  private CommunityServiceImpl communityService;

  private Community testCommunity;

  @BeforeEach
  void setUp() {
    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();
  }

  @Nested
  class GetCommunityTests {
    @Test
    void get_whenCommunityExists_thenReturnCommunity() {
      when(communityDao.find(TEST_COMMUNITY_ID)).thenReturn(Optional.of(testCommunity));

      Community result = communityService.get(TEST_COMMUNITY_ID);

      assertThat(result).isEqualTo(testCommunity);
      verify(communityDao).find(TEST_COMMUNITY_ID);
    }

    @Test
    void get_whenCommunityNotExists_thenThrowException() {
      when(communityDao.find(TEST_COMMUNITY_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> communityService.get(TEST_COMMUNITY_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Сообщество не найдено");
    }
  }

  @Nested
  class GetAllCommunitiesTests {
    @Test
    void getAll_whenCommunitiesExist_thenReturnCommunities() {
      List<Community> expected = List.of(testCommunity);
      when(communityDao.getAll()).thenReturn(expected);

      List<Community> result = communityService.getAll();

      assertThat(result).isEqualTo(expected);
      verify(communityDao).getAll();
    }
  }

  @Test
  void save_whenValidCommunity_thenReturnSavedCommunity() {
    when(communityDao.saveOrUpdate(testCommunity)).thenReturn(testCommunity);

    Community result = communityService.save(testCommunity);

    assertThat(result).isEqualTo(testCommunity);
    verify(communityDao).saveOrUpdate(testCommunity);
  }
}

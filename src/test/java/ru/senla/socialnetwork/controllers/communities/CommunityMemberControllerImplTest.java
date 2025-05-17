package ru.senla.socialnetwork.controllers.communities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.communities.impl.CommunityMemberControllerImpl;
import ru.senla.socialnetwork.dto.communitites.BanCommunityMemberDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.communities.CommunityMemberFacade;

import java.util.List;

import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.model.MemberRole;

@ExtendWith(MockitoExtension.class)
class CommunityMemberControllerImplTest {

  @Mock
  private CommunityMemberFacade communityMemberFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private CommunityMemberControllerImpl communityMemberController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private CommunityMemberDTO testMember;
  private BanCommunityMemberDTO testBanDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(communityMemberController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testMember = new CommunityMemberDTO(
        TEST_USER_ID_1,
        TEST_EMAIL_1,
        MemberRole.MEMBER,
        TEST_COMMUNITY_ID,
        TEST_DATE,
        null,
        false,
        null
    );

    testBanDTO = new BanCommunityMemberDTO(
        TEST_EMAIL_2,
        TEST_BAN_REASON
    );
  }

  @Nested
  class GetAllMembersTests {
    @Test
    void getAll_shouldReturnMembersList() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.getAll(TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(List.of(testMember));

      mockMvc.perform(get("/communities/{communityId}/members", TEST_COMMUNITY_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].email").value(TEST_EMAIL_1))
          .andExpect(jsonPath("$[0].communityId").value(TEST_COMMUNITY_ID));
    }
  }

  @Nested
  class JoinCommunityTests {
    @Test
    void joinCommunity_shouldReturnMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.joinCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(testMember);

      mockMvc.perform(post("/communities/{communityId}/members", TEST_COMMUNITY_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class LeaveCommunityTests {
    @Test
    void leaveCommunity_shouldReturnMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.leaveCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(testMember);

      mockMvc.perform(delete("/communities/{communityId}/members", TEST_COMMUNITY_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class BanMemberTests {
    @Test
    void banMember_shouldReturnBannedMember() throws Exception {
      CommunityMemberDTO bannedMember = new CommunityMemberDTO(
          1L,
          TEST_EMAIL_2,
          MemberRole.MEMBER,
          TEST_COMMUNITY_ID,
          TEST_DATE,
          null,
          true,
          TEST_BAN_REASON
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.banMember(
          eq(TEST_COMMUNITY_ID),
          eq(TEST_EMAIL_2),
          eq(TEST_BAN_REASON),
          eq(TEST_EMAIL_1)))
          .thenReturn(bannedMember);

      mockMvc.perform(post("/communities/{communityId}/members/ban", TEST_COMMUNITY_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testBanDTO))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.isBanned").value(true))
          .andExpect(jsonPath("$.bannedReason").value(TEST_BAN_REASON));
    }

    @Test
    void banMember_shouldReturnBadRequestForInvalidEmail() throws Exception {
      BanCommunityMemberDTO invalidDTO = new BanCommunityMemberDTO("invalid-email", TEST_BAN_REASON);

      mockMvc.perform(post("/communities/{communityId}/members/ban", TEST_COMMUNITY_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class UnbanMemberTests {
    @Test
    void unbanMember_shouldReturnUnbannedMember() throws Exception {
      CommunityMemberDTO unbannedMember = new CommunityMemberDTO(
          1L,
          TEST_EMAIL_2,
          MemberRole.MEMBER,
          TEST_COMMUNITY_ID,
          TEST_DATE,
          null,
          false,
          null
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.unbanMember(TEST_COMMUNITY_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .thenReturn(unbannedMember);

      mockMvc.perform(post("/communities/{communityId}/members/unban", TEST_COMMUNITY_ID)
              .param("email", TEST_EMAIL_2)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.isBanned").value(false));
    }
  }

  @Nested
  class ChangeMemberRoleTests {
    @Test
    void changeMemberRole_shouldReturnUpdatedMember() throws Exception {
      CommunityMemberDTO adminMember = new CommunityMemberDTO(
          1L,
          TEST_EMAIL_2,
          MemberRole.ADMIN,
          TEST_COMMUNITY_ID,
          TEST_DATE,
          null,
          false,
          null
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityMemberFacade.changeMemberRole(
          TEST_COMMUNITY_ID, TEST_EMAIL_2, MemberRole.ADMIN, TEST_EMAIL_1))
          .thenReturn(adminMember);

      mockMvc.perform(patch("/communities/{communityId}/members/role", TEST_COMMUNITY_ID)
              .param("email", TEST_EMAIL_2)
              .param("role", "ADMIN")
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void changeMemberRole_shouldReturnBadRequestForInvalidRole() throws Exception {
      mockMvc.perform(patch("/communities/{communityId}/members/role", TEST_COMMUNITY_ID)
              .param("email", TEST_EMAIL_2)
              .param("role", "INVALID_ROLE"))
          .andExpect(status().isBadRequest());
    }
  }
}

import com.example.polls.model.User;
import com.example.polls.payload.UserProfile;
import com.example.polls.payload.UserSummary;
import com.example.polls.payload.UserIdentityAvailability;
import com.example.polls.repository.PollRepository;
import com.example.polls.repository.UserRepository;
import com.example.polls.repository.VoteRepository;
import com.example.polls.security.UserPrincipal;
import com.example.polls.service.PollService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PollRepository pollRepository;

    @MockBean
    private VoteRepository voteRepository;

    @MockBean
    private PollService pollService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetCurrentUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckUsernameAvailability() throws Exception {
        String username = "testUser";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/checkUsernameAvailability")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testCheckEmailAvailability() throws Exception {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/checkEmailAvailability")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testGetUserProfile() throws Exception {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPollsCreatedBy() throws Exception {
        String username = "testUser";
        when(pollService.getPollsCreatedBy(username, null, 1, 10)).thenReturn(new PagedResponse<>());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{username}/polls", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPollsVotedBy() throws Exception {
        String username = "testUser";
        when(pollService.getPollsVotedBy(username, null, 1, 10)).thenReturn(new PagedResponse<>());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{username}/votes", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

import com.example.polls.model.Poll;
import com.example.polls.payload.PollRequest;
import com.example.polls.payload.VoteRequest;
import com.example.polls.repository.PollRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PollController.class)
public class PollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PollRepository pollRepository;

    @MockBean
    private PollService pollService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetPolls() throws Exception {
        // Mock the poll list to return when pollService.getAllPolls is called
        List<Poll> pollList = new ArrayList<>();
        when(pollService.getAllPolls(any(UserPrincipal.class), anyInt(), anyInt())).thenReturn(pollList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/polls")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testCreatePoll() throws Exception {
        PollRequest pollRequest = new PollRequest();
        pollRequest.setQuestion("Test Question");
        pollRequest.setChoices(new ArrayList<>());

        when(pollService.createPoll(ArgumentMatchers.any(PollRequest.class))).thenReturn(new Poll());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/polls")
                        .content(asJsonString(pollRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetPollById() throws Exception {
        long pollId = 1L;
        when(pollService.getPollById(pollId, null)).thenReturn(new Poll());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/polls/{pollId}", pollId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testCastVote() throws Exception {
        long pollId = 1L;
        VoteRequest voteRequest = new VoteRequest();

        when(pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, null)).thenReturn(new Poll());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/polls/{pollId}/votes", pollId)
                        .content(asJsonString(voteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

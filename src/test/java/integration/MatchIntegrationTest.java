package integration;

import app.foot.FootApi;
import app.foot.controller.rest.*;
import app.foot.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.assertThrowsExceptionMessage;
import static utils.TestUtils.checkApiException;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
class MatchIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();  //Allow 'java.time.Instant' mapping
    private final Logger logger = Logger.getLogger(MatchIntegrationTest.class.getName());

    @Test
    void read_match_by_id_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch2(), actual);
    }
    @Test
    void read_matches_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Match> actual = objectMapper.readValue(
                response.getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Match.class));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(3, actual.size());
        logger.info(expectedMatch2().toString());
        assertTrue(actual.contains(expectedMatch2()));
    }
    @Test
    void add_goal_in_match_ok() throws Exception {
        String matchId = "3";
        MockHttpServletResponse response = mockMvc.perform(post("/matches/"+matchId+"/goals")
                        .content(objectMapper.writeValueAsString(List.of(playerScorerMachId3())))
                        .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch3(), actual);
    }
    @Test
    void add_goal_in_match_too_small_time_ko() throws Exception {
        String matchId = "3";
        String errorMessage = "400 BAD_REQUEST : Player#"+playerScorerMachId3().getPlayer().getId()+" cannot score before before minute 0.";
        assertThrowsExceptionMessage(errorMessage, ApiException.class,
                ()-> checkApiException(mockMvc.perform(post("/matches/"+matchId+"/goals")
                        .content(objectMapper.writeValueAsString(List.of(playerScorerMachId3().toBuilder().scoreTime(-1).build())))
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()));
    }
    @Test
    void add_goal_in_match_too_big_time_ko() throws Exception {
        String matchId = "3";
        String errorMessage = "400 BAD_REQUEST : Player#J"+playerScorerMachId3().getPlayer().getId()+" cannot score before after minute 90.";
        assertThrowsExceptionMessage(errorMessage, ApiException.class,
                ()-> checkApiException(mockMvc.perform(post("/matches/"+matchId+"/goals")
                                .content(objectMapper.writeValueAsString(List.of(playerScorerMachId3().toBuilder().scoreTime(99).build())))
                                .contentType("application/json"))
                        .andExpect(status().isBadRequest()).andReturn()
                        .getResponse())
        );
    }
    @Test
    void add_goal_in_match_without_time_ko() throws Exception {
        String matchId = "3";
        String errorMessage = "400 BAD_REQUEST : Score minute is mandatory.";
        assertThrowsExceptionMessage(errorMessage, ApiException.class,
                ()-> checkApiException(mockMvc.perform(post("/matches/"+matchId+"/goals")
                                .content(objectMapper.writeValueAsString(List.of(playerScorerMachId3().toBuilder().scoreTime(null).build())))
                                .contentType("application/json"))
                        .andExpect(status().isBadRequest()).andReturn()
                        .getResponse())
        );
    }
    @Test
    void add_goal_in_match_by_guardian_ko() throws Exception {
        String matchId = "3";
        String errorMessage = "400 BAD_REQUEST : Player#"+player9().getId()+" is a guardian so they cannot score.";
        assertThrowsExceptionMessage(errorMessage, ApiException.class,
                ()-> checkApiException(mockMvc.perform(post("/matches/"+matchId+"/goals")
                                .content(objectMapper.writeValueAsString(List.of(playerScorerMachId3().toBuilder().player(player9()).build())))
                                .contentType("application/json"))
                        .andExpect(status().isBadRequest()).andReturn().getResponse())
        );
    }
    @Test
    void read_match_by_id_ko() throws Exception {
        String matchId = "20";
        String errorMessage = "404 NOT_FOUND : Match#"+matchId+" not found.";
        assertThrowsExceptionMessage(errorMessage, ApiException.class,
                ()->checkApiException(mockMvc.perform(get("/matches/"+matchId)).andExpect(status().isNotFound()).andReturn().getResponse())
        );
    }
    private static int  numberAddingPostGoal=1;
    private static List<PlayerScorer> teamMatchAMatch3(int numberAddingPostGoal) {
        List<PlayerScorer> teamMatches = new ArrayList<>();
        for (int i = 0; i < numberAddingPostGoal; i++) {
            teamMatches.add(i, playerScorerMachId3());
        }
        return teamMatches;
    }

    private static Match expectedMatch2() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchB())
                .stadium("S2")
                .datetime(Instant.parse("2023-01-01T14:00:00Z"))
                .build();
    }
    private static Match expectedMatch3() {
        return Match.builder()
                .id(3)
                .teamA(teamMatchAMatch3())
                .teamB(teamMatchBMatch3())
                .stadium("S3")
                .datetime(Instant.parse("2023-01-01T18:00:00Z"))
                .build();
    }
    private static TeamMatch teamMatchB() {
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchAMatch3() {
        return TeamMatch.builder()
                .team(team1())
                .score(0)
                .scorers(List.of())
                .build();
    }
    private static TeamMatch teamMatchBMatch3() {
        return TeamMatch.builder()
                .team(team3())
                .score(numberAddingPostGoal)
                .scorers(teamMatchAMatch3(numberAddingPostGoal))
                .build();
    }

    private static TeamMatch teamMatchA() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(70)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(80)
                                .isOG(true)
                                .build()))
                .build();
    }

    private static Team team3() {
        return Team.builder()
                .id(3)
                .name("E3")
                .build();
    }

    private static Team team1() {
        return Team.builder()
                .id(1)
                .name("E1")
                .build();
    }

    private static Player player6() {
        return Player.builder()
                .id(6)
                .name("J6")
                .isGuardian(false)
                .teamName("E3")
                .build();
    }
    private static Player player9() {
        return Player.builder()
                .id(9)
                .name("G3")
                .isGuardian(true)
                .teamName("E3")
                .build();
    }

    private static Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .teamName("E2")
                .isGuardian(false)
                .build();
    }

    private static Team team2() {
        return Team.builder()
                .id(2)
                .name("E2")
                .build();
    }
    private static PlayerScorer playerScorerMachId3() {
        return PlayerScorer.builder()
                .player(player6())
                .scoreTime(53)
                .isOG(false)
                .build();
    }
}

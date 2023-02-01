package integration;

import app.foot.FootApi;
import app.foot.controller.rest.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.assertThrowsExceptionMessage;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
class PlayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Player player1() {
        return Player.builder()
                .id(1)
                .name("J1")
                .isGuardian(false)
                .teamName("E1")
                .build();
    }

    Player player2() {
        return Player.builder()
                .id(2)
                .name("J2")
                .isGuardian(false)
                .teamName("E1")
                .build();
    }

    Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .teamName("E2")
                .build();
    }

    @Test
    void read_players_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/players"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(9, actual.size());
        assertTrue(actual.containsAll(List.of(
                player2(),
                player3())));
    }

    @Test
    void create_players_ok() throws Exception {
        Player toCreate = Player.builder()
                .id(1)
                .name("Joe Doe")
                .isGuardian(true)
                .teamName("E1")
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .content(objectMapper.writeValueAsString(List.of(toCreate)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toCreate, actual.get(0));
    }
    @Test
    void update_players_ok() throws Exception {
        Player toCreate = player1().toBuilder().name("new").isGuardian(player1().getIsGuardian()==false).build();
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .content(objectMapper.writeValueAsString(List.of(toCreate)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toCreate, actual.get(0));
    }
    @Test
    void update_players_with_name_empty_ko() throws Exception {
        String errorMessage = "Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : Player#"+player1().getId()+" have to have name.";
        assertThrowsExceptionMessage(errorMessage, ServletException.class,
                ()->mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(player1().toBuilder().name("").build())))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound())
        );
    }
    @Test
    void update_players_with_name_null_ko() throws Exception {
        String errorMessage = "Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : Player#"+player1().getId()+" have to have name.";
        assertThrowsExceptionMessage(errorMessage, ServletException.class,
                ()->mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(player1().toBuilder().name(null).build())))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound())
        );
    }
    @Test
    void update_players_with_team_name_change_ko() throws Exception {
        String errorMessage = "Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : Team can't change in Player with id:"+player1().getId()+".";
        assertThrowsExceptionMessage(errorMessage, ServletException.class,
                ()->mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(player1().toBuilder().teamName(player1().getTeamName()+"change").build())))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound())
        );
    }
    @Test
    void update_players_with_bad_id_ko() throws Exception {
        int bad_id = 5000;
        String errorMessage = "Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : can't update new id "+bad_id+".";
        assertThrowsExceptionMessage(errorMessage, ServletException.class,
                ()->mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(player1().toBuilder().id(bad_id).build())))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound())
        );
    }
    @Test
    void update_players_without_id_ko() throws Exception {
        String errorMessage = "Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : Player#null have to have id.";
        assertThrowsExceptionMessage(errorMessage, ServletException.class,
                ()->mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(player1().toBuilder().id(null).build())))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound())
        );
    }

    private List<Player> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }


}

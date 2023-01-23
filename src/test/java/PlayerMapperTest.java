import app.foot.model.Match;
import app.foot.model.Player;
import app.foot.model.PlayerScorer;
import app.foot.repository.MatchRepository;
import app.foot.repository.PlayerRepository;
import app.foot.repository.entity.MatchEntity;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.TeamEntity;
import app.foot.repository.mapper.PlayerMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//TODO-2: complete these tests
public class PlayerMapperTest {
    MatchRepository matchRepository = mock(MatchRepository.class);
    PlayerRepository playerRepository = mock(PlayerRepository.class);
    PlayerMapper playerMapperMock = mock(PlayerMapper.class);
    PlayerMapper subject = new PlayerMapper(matchRepository,playerRepository);
    @Test
    void player_to_domain_ok() {
        Player expected = player();
        Player actual = subject.toDomain(playerEntity());
        assertEquals(expected, actual);
    }

    @Test
    void player_scorer_to_domain_ok() {
        when(playerMapperMock.toDomain(playerEntity())).thenReturn(player());
        PlayerScorer expected = playerScore();
        PlayerScorer actual = subject.toDomain(playerScoreEntity());
        assertEquals(expected, actual);
    }

    @Test
    void player_scorer_to_entity_ok() {
        when(matchRepository.findById(1)).thenReturn(Optional.ofNullable(MatchEntity.builder().build()));
        when(playerRepository.findById(1)).thenReturn(Optional.ofNullable(playerEntity()));
        PlayerScoreEntity expected = playerScoreEntity();
        PlayerScoreEntity actual = subject.toEntity(1,playerScore());
        assertEquals(expected, actual);
    }
    private static TeamEntity teamEntity() {
        return TeamEntity.builder()
                .id(1)
                .name("team name")
                .build();
    }
    private static PlayerEntity playerEntity() {
        return PlayerEntity.builder()
                .id(1)
                .name("player name")
                .team(teamEntity())
                .guardian(true)
                .build();
    }
    private static Player player() {
        return Player.builder()
                .id(1)
                .name("player name")
                .teamName("team name")
                .isGuardian(true)
                .build();
    }
    private static PlayerScoreEntity playerScoreEntity() {
        return PlayerScoreEntity.builder()
                .id(1)
                .minute(45)
                .player(playerEntity())
                .match(MatchEntity.builder().build())
                .ownGoal(true)
                .build();
    }
    private static PlayerScorer playerScore() {
        return PlayerScorer.builder()
                .minute(45)
                .player(player())
                .isOwnGoal(true)
                .build();
    }
}

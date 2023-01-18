package app.foot.repository.mapper;

import app.foot.model.PlayerScorer;
import app.foot.model.Team;
import app.foot.repository.MatchRepository;
import app.foot.repository.entity.MatchEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.TeamEntity;
import app.foot.repository.entity.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerScoreMapper {
    private final PlayerMapper playerMapper;
    private final MatchRepository matchRepository;
    public PlayerScorer toDomain(PlayerScoreEntity entity) {
        return PlayerScorer.builder()
                .player(playerMapper.toDomain(entity.getPlayer()))
                .minute(entity.getMinute())
                .isOwnGoal(entity.isOwnGoal())
                .build();
    }
    public PlayerScoreEntity newDomaintoEntity(PlayerScorer domain, int matchId) {
        PlayerScoreEntity playerScoreEntity = new PlayerScoreEntity();
        MatchEntity match = matchRepository.findById(matchId).orElseThrow(
                ()-> new NotFoundException("math not found")
        );
        playerScoreEntity.setPlayer(playerMapper.toEntity(domain.getPlayer()));
        playerScoreEntity.setMinute(domain.getMinute());
        playerScoreEntity.setMatch(match);
        playerScoreEntity.setOwnGoal(domain.getIsOwnGoal());
        return playerScoreEntity;
    }
}

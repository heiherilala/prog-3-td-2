package app.foot.service;

import app.foot.model.Match;
import app.foot.model.Player;
import app.foot.model.PlayerScorer;
import app.foot.repository.MatchRepository;
import app.foot.repository.PlayerRepository;
import app.foot.repository.PlayerScoreRepository;
import app.foot.repository.entity.MatchEntity;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.exception.BadRequestException;
import app.foot.repository.entity.exception.NotFoundException;
import app.foot.repository.entity.validator.PlayeScoreEntityValidator;
import app.foot.repository.mapper.MatchMapper;
import app.foot.repository.mapper.PlayerMapper;
import app.foot.repository.mapper.PlayerScoreMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class PlayerScoreService {
    private final PlayerScoreRepository repository;
    private final PlayerScoreMapper mapper;
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final PlayeScoreEntityValidator playeScoreEntityValidator;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    @Transactional
    public Match addPlayerScorer(List<PlayerScorer> playerScorers , int matchId) {
        List<PlayerScoreEntity> playerScoreEntities = new ArrayList<>();
        MatchEntity match = matchRepository.findById(matchId).orElseThrow(
                ()->new NotFoundException("math not found")
        );
        for (PlayerScorer playerScorer: playerScorers) {
            PlayerScoreEntity playerScoreEntity = mapper.newDomaintoEntity(playerScorer, matchId);

            PlayerEntity playerEntityConcerned = playerRepository.findById(playerScorer.getPlayer().getId()).orElseThrow(
                    () -> new NotFoundException("player not found")
            );
            /*
            Player addedPlayer = playerMapper.toDomain(playerEntityConcerned);
            if (!addedPlayer.equals(playerScorer.getPlayer())) {
                throw new BadRequestException("player number "+ playerEntityConcerned.getId() + " is not the same as in base");
            }
            */
            if ((playerEntityConcerned.getTeam().getId() != match.getTeamB().getId())&&(playerEntityConcerned.getTeam().getId() != match.getTeamA().getId())) {
                throw new BadRequestException("player N°" + playerScorer.getPlayer().getId() + "is not in TeamA or in TeamB");
            }
            playeScoreEntityValidator.accept(playerScoreEntity);
            playerScoreEntities.add(playerScoreEntity);
        }

        repository.saveAll(playerScoreEntities);
        MatchEntity newmatch = matchRepository.findById(matchId).orElseThrow(
                ()->new NotFoundException("math not found")
        );
        return matchMapper.toDomain(newmatch);
    }
}

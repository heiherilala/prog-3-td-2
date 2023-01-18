package app.foot.repository.mapper;

import app.foot.model.Player;
import app.foot.repository.PlayerRepository;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerMapper {
    private final PlayerRepository playerRepository;
    public Player toDomain(PlayerEntity entity) {
        return Player.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isGuardian(entity.isGuardian())
                .build();
    }
    public PlayerEntity toEntity(Player domain) {
        return playerRepository.findById(domain.getId()).orElseThrow(
                ()-> new NotFoundException("not found player")
        );
    }
}

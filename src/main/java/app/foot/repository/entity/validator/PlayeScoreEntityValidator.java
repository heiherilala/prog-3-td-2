package app.foot.repository.entity.validator;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import app.foot.repository.entity.exception.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import app.foot.repository.entity.PlayerScoreEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayeScoreEntityValidator implements Consumer<PlayerScoreEntity> {
  private final Validator validator;

  public void accept(List<PlayerScoreEntity> playerScoreEntitys) {
    playerScoreEntitys.forEach(this::accept);
  }

  @Override public void accept(PlayerScoreEntity playerScoreEntity) {

    Set<ConstraintViolation<PlayerScoreEntity>> violations = validator.validate(playerScoreEntity);
    if (playerScoreEntity.getPlayer().isGuardian()) {
      throw new BadRequestException("gardian can't have goal");
    }
    if (playerScoreEntity.getMinute()>90) {
      throw new BadRequestException("minute must be less than 90");
    }
    if (playerScoreEntity.getMinute()<0) {
      throw new BadRequestException("minute must be more than 0");
    }
    if (!violations.isEmpty()) {
      String constraintMessages = violations
          .stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.joining(". "));
      throw new BadRequestException(constraintMessages);
    }
  }
}

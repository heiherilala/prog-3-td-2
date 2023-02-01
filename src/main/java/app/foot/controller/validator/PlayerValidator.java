package app.foot.controller.validator;

import app.foot.exception.BadRequestException;
import app.foot.model.Player;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PlayerValidator implements Consumer<Player> {

    @Override
    public void accept(Player player) {
        StringBuilder exceptionBuilder = new StringBuilder();
        if (player.getName()==""||player.getName()==null) {
            exceptionBuilder.append("Player#")
                    .append(player.getId())
                    .append(" have to have ").append("name.");
        }
        if (player.getId()==null) {
            exceptionBuilder.append("Player#")
                    .append(player.getId())
                    .append(" have to have ").append("id.");
        }
        if (!exceptionBuilder.isEmpty()) {
            throw new BadRequestException(exceptionBuilder.toString());
        }
    }
}

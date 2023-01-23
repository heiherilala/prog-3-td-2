import app.foot.controller.rest.Player;
import app.foot.controller.rest.PlayerScorer;
import app.foot.controller.validator.GoalValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO-1: complete these tests
public class GoalValidatorTest {
    GoalValidator subject = new GoalValidator();

    @Test
    void accept_ok() {
        assertDoesNotThrow(()->{subject.accept(playerScorerRigth());});
    }

    //Mandatory attributes not provided : scoreTime
    @Test
    void accept_ko() {
        assertThrows(RuntimeException.class,()->{subject.accept(playerScorerNullScoreTime());});
    }

    @Test
    void when_guardian_throws_exception() {
        assertThrows(RuntimeException.class,()->{subject.accept(playerScorerGardian());});
    }

    @Test
    void when_score_time_greater_than_90_throws_exception() {
        assertThrows(RuntimeException.class,()->{subject.accept(playerScorerGigScoreTime());});
    }

    @Test
    void when_score_time_less_than_0_throws_exception() {
        assertThrows(RuntimeException.class,()->{subject.accept(playerScorerLessscoreTime());});
    }
    private static PlayerScorer playerScorerRigth(){
        return PlayerScorer.builder()
                .player(Player.builder().isGuardian(false).build())
                .scoreTime(45)
                .build();
    }
    private static PlayerScorer playerScorerNullScoreTime(){
        return PlayerScorer.builder()
                .player(Player.builder().isGuardian(false).build())
                .build();
    }
    private static PlayerScorer playerScorerGardian(){
        return PlayerScorer.builder()
                .player(Player.builder().isGuardian(true).build())
                .scoreTime(45)
                .build();
    }
    private static PlayerScorer playerScorerGigScoreTime(){
        return PlayerScorer.builder()
                .player(Player.builder().isGuardian(false).build())
                .scoreTime(100)
                .build();
    }
    private static PlayerScorer playerScorerLessscoreTime(){
        return PlayerScorer.builder()
                .player(Player.builder().isGuardian(false).build())
                .scoreTime(-8)
                .build();
    }
}

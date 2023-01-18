package app.foot.controller;

import app.foot.model.Match;
import app.foot.model.PlayerScorer;
import app.foot.service.PlayerScoreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class PlayerScoreController {
    private final PlayerScoreService service;

    @PostMapping("/matches/{idMatch}/goals")
    public Match addPlayerScore(@RequestBody List<PlayerScorer> playerScorers, @PathVariable int idMatch) {
        return service.addPlayerScorer(playerScorers,idMatch);
    }
}

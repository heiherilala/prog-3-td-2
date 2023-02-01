package app.foot.service;

import app.foot.controller.validator.PlayerValidator;
import app.foot.exception.BadRequestException;
import app.foot.model.Player;
import app.foot.repository.PlayerRepository;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.mapper.PlayerMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;
    private final PlayerMapper mapper;
    private final PlayerValidator validator;

    public List<Player> getPlayers() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Player> createPlayers(List<Player> toCreate) {
        return repository.saveAll(toCreate.stream()
                        .map(mapper::toEntity)
                        .collect(Collectors.toUnmodifiableList())).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toUnmodifiableList());
    }
    @Transactional
    public List<Player> updatePlayers(List<Player> toUpdate) {
        for (int i = 0; i < toUpdate.size(); i++) {
            validator.accept(toUpdate.get(i));
            int finalI = i;
            PlayerEntity playerEntityactual = repository.findById(toUpdate.get(i).getId()).orElseThrow(
                    ()->{throw new BadRequestException("can't update new id "+ toUpdate.get(finalI).getId() +".");}
            );
            if ( !toUpdate.get(i).getTeamName().equals(mapper.toDomain(playerEntityactual).getTeamName())) {
                throw new BadRequestException("Team can't change in Player with id:"+toUpdate.get(i).getId()+".");
            }
            if (toUpdate.get(i).getId() == null) {
                throw new BadRequestException("Player have to have id.");
            }
        }
        return repository.saveAll(toUpdate.stream()
                        .map(mapper::toEntity)
                        .collect(Collectors.toUnmodifiableList()))
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toUnmodifiableList());
    }
}

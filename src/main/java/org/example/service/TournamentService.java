package org.example.service;

import org.example.annotation.Cumponent;
import org.example.dto.TournamentRequest;
import org.example.factory.TournamentFactory;
import org.example.model.Tournament;
import org.example.repository.MatchRepository;
import org.example.repository.TeamApplicationRepository;
import org.example.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Cumponent
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TeamApplicationRepository applicationRepository;
    private final MatchRepository matchRepository;
    private final TournamentFactory tournamentFactory = new TournamentFactory();

    public TournamentService(
            TournamentRepository tournamentRepository,
            TeamApplicationRepository applicationRepository,
            MatchRepository matchRepository
    ) {
        this.tournamentRepository = tournamentRepository;
        this.applicationRepository = applicationRepository;
        this.matchRepository = matchRepository;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Турнир не найден: " + id));
    }

    public Tournament createTournament(TournamentRequest request) {
        validateRequest(request);

        Tournament tournament = tournamentFactory.create(
                request.getName(),
                request.getTeamLimit(),
                "PLANNED"
        );

        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long id, TournamentRequest request) {
        validateRequest(request);

        Tournament tournament = getTournamentById(id);

        tournament.setName(request.getName());
        tournament.setTeamLimit(request.getTeamLimit());

        return tournamentRepository.save(tournament);
    }

    public Tournament patchTournament(Long id, TournamentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        Tournament tournament = getTournamentById(id);

        if (request.getName() != null) {
            validateName(request.getName());
            tournament.setName(request.getName());
        }

        if (request.getTeamLimit() != null) {
            validateTeamLimit(request.getTeamLimit());
            tournament.setTeamLimit(request.getTeamLimit());
        }

        return tournamentRepository.save(tournament);
    }

    public void deleteTournament(Long id) {
        Tournament tournament = getTournamentById(id);

        boolean hasApplications = applicationRepository.findAll().stream()
                .anyMatch(application ->
                        application.getTournament().getId().equals(id));

        if (hasApplications) {
            throw new IllegalArgumentException(
                    "Нельзя удалить турнир: существуют связанные заявки");
        }

        boolean hasMatches = matchRepository.findAll().stream()
                .anyMatch(match ->
                        match.getTournament().getId().equals(id));

        if (hasMatches) {
            throw new IllegalArgumentException(
                    "Нельзя удалить турнир: существуют связанные матчи");
        }

        tournamentRepository.deleteById(tournament.getId());
    }

    private void validateRequest(TournamentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        validateName(request.getName());

        if (request.getTeamLimit() == null) {
            throw new IllegalArgumentException(
                    "Необходимо указать teamLimit");
        }

        validateTeamLimit(request.getTeamLimit());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Название турнира не может быть пустым");
        }
    }

    private void validateTeamLimit(Integer teamLimit) {
        if (teamLimit == null || teamLimit <= 0) {
            throw new IllegalArgumentException(
                    "Ограничение количества команд должно быть больше нуля");
        }
    }
}
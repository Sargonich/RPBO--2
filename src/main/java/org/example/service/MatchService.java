package org.example.service;

import org.example.annotation.Cumponent;
import org.example.dto.MatchRequest;
import org.example.factory.MatchFactory;
import org.example.model.Match;
import org.example.model.Team;
import org.example.model.Tournament;
import org.example.repository.MatchRepository;
import org.example.repository.TeamRepository;
import org.example.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Cumponent
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchFactory matchFactory = new MatchFactory();

    public MatchService(
            MatchRepository matchRepository,
            TeamRepository teamRepository,
            TournamentRepository tournamentRepository
    ) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Матч не найден: " + id));
    }

    public Match createMatch(MatchRequest request) {
        validateRequest(request);

        Tournament tournament = findTournament(request.getTournamentId());
        Team team1 = findTeam(request.getTeam1Id());
        Team team2 = findTeam(request.getTeam2Id());

        validateTeams(team1, team2);

        Match match = matchFactory.create(
                tournament,
                team1,
                team2,
                request.getScore1(),
                request.getScore2(),
                request.getStatus()
        );

        return matchRepository.save(match);
    }

    public Match updateMatch(Long id, MatchRequest request) {
        validateRequest(request);

        Match match = getMatchById(id);
        Tournament tournament = findTournament(request.getTournamentId());
        Team team1 = findTeam(request.getTeam1Id());
        Team team2 = findTeam(request.getTeam2Id());

        validateTeams(team1, team2);

        match.setTournament(tournament);
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setScore1(request.getScore1());
        match.setScore2(request.getScore2());
        match.setStatus(request.getStatus());

        return matchRepository.save(match);
    }

    public Match patchMatch(Long id, MatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        Match match = getMatchById(id);

        Tournament tournament = request.getTournamentId() != null
                ? findTournament(request.getTournamentId())
                : match.getTournament();

        Team team1 = request.getTeam1Id() != null
                ? findTeam(request.getTeam1Id())
                : match.getTeam1();

        Team team2 = request.getTeam2Id() != null
                ? findTeam(request.getTeam2Id())
                : match.getTeam2();

        validateTeams(team1, team2);

        if (request.getScore1() != null) {
            validateScore(request.getScore1());
        }

        if (request.getScore2() != null) {
            validateScore(request.getScore2());
        }

        if (request.getStatus() != null && request.getStatus().isBlank()) {
            throw new IllegalArgumentException(
                    "Статус матча не может быть пустым");
        }

        match.setTournament(tournament);
        match.setTeam1(team1);
        match.setTeam2(team2);

        if (request.getScore1() != null) {
            match.setScore1(request.getScore1());
        }

        if (request.getScore2() != null) {
            match.setScore2(request.getScore2());
        }

        if (request.getStatus() != null) {
            match.setStatus(request.getStatus());
        }

        return matchRepository.save(match);
    }

    public void deleteMatch(Long id) {
        getMatchById(id);
        matchRepository.deleteById(id);
    }

    private Tournament findTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Турнир не найден: " + id));
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Команда не найдена: " + id));
    }

    private void validateRequest(MatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        if (request.getTournamentId() == null
                || request.getTeam1Id() == null
                || request.getTeam2Id() == null
                || request.getScore1() == null
                || request.getScore2() == null
                || request.getStatus() == null
                || request.getStatus().isBlank()) {
            throw new IllegalArgumentException(
                    "Необходимо заполнить все поля матча");
        }

        validateScore(request.getScore1());
        validateScore(request.getScore2());
    }

    private void validateScore(Integer score) {
        if (score == null || score < 0) {
            throw new IllegalArgumentException(
                    "Счёт матча не может быть отрицательным");
        }
    }

    private void validateTeams(Team team1, Team team2) {
        if (team1.getId().equals(team2.getId())) {
            throw new IllegalArgumentException(
                    "Одна команда не может играть сама с собой");
        }
    }
}
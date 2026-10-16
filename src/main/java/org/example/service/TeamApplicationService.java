package org.example.service;

import org.example.annotation.Cumponent;
import org.example.dto.TeamApplicationRequest;
import org.example.factory.TeamApplicationFactory;
import org.example.model.Team;
import org.example.model.TeamApplication;
import org.example.model.Tournament;
import org.example.repository.TeamApplicationRepository;
import org.example.repository.TeamRepository;
import org.example.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Cumponent
@Service
public class TeamApplicationService {

    private final TeamApplicationRepository applicationRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamApplicationFactory applicationFactory =
            new TeamApplicationFactory();

    public TeamApplicationService(
            TeamApplicationRepository applicationRepository,
            TeamRepository teamRepository,
            TournamentRepository tournamentRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public List<TeamApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public TeamApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Заявка не найдена: " + id));
    }

    public TeamApplication createApplication(TeamApplicationRequest request) {
        validateRequest(request);

        Team team = findTeam(request.getTeamId());
        Tournament tournament = findTournament(request.getTournamentId());

        validateNoDuplicate(team.getId(), tournament.getId(), null);

        TeamApplication application = applicationFactory.create(
                team,
                tournament,
                "PENDING"
        );

        return applicationRepository.save(application);
    }

    public TeamApplication updateApplication(
            Long id,
            TeamApplicationRequest request
    ) {
        validateRequest(request);

        TeamApplication application = getApplicationById(id);
        Team team = findTeam(request.getTeamId());
        Tournament tournament = findTournament(request.getTournamentId());

        validateNoDuplicate(team.getId(), tournament.getId(), id);

        application.setTeam(team);
        application.setTournament(tournament);

        return applicationRepository.save(application);
    }

    public TeamApplication patchApplication(
            Long id,
            TeamApplicationRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        TeamApplication application = getApplicationById(id);

        Team team = request.getTeamId() != null
                ? findTeam(request.getTeamId())
                : application.getTeam();

        Tournament tournament = request.getTournamentId() != null
                ? findTournament(request.getTournamentId())
                : application.getTournament();

        validateNoDuplicate(team.getId(), tournament.getId(), id);

        application.setTeam(team);
        application.setTournament(tournament);

        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        getApplicationById(id);
        applicationRepository.deleteById(id);
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Команда не найдена: " + id));
    }

    private Tournament findTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Турнир не найден: " + id));
    }

    private void validateNoDuplicate(
            Long teamId,
            Long tournamentId,
            Long excludedApplicationId
    ) {
        boolean duplicate = applicationRepository.findAll().stream()
                .anyMatch(application ->
                        !application.getId().equals(excludedApplicationId)
                                && application.getTeam().getId().equals(teamId)
                                && application.getTournament().getId()
                                .equals(tournamentId));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "У команды уже есть заявка на этот турнир");
        }
    }

    private void validateRequest(TeamApplicationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса отсутствует");
        }

        if (request.getTeamId() == null) {
            throw new IllegalArgumentException("Необходимо указать teamId");
        }

        if (request.getTournamentId() == null) {
            throw new IllegalArgumentException(
                    "Необходимо указать tournamentId");
        }
    }
}
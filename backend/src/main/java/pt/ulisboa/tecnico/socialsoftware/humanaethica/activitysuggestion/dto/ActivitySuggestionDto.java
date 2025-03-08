package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import jakarta.persistence.*;
import java.time.LocalDateTime;

public class ActivitySuggestionDto {
    private Integer id;
    private String name;
    private String description;
    private String region;
    private LocalDateTime creationDate;
    private LocalDateTime applicationDeadline;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private Integer participantsNumberLimit;
    private State state;
    private Integer institutionId;
    private Integer volunteerId;

    public ActivitySuggestionDto() {}

    public ActivitySuggestionDto(ActivitySuggestion activitySuggestion) {
        this.id = activitySuggestion.getId();
        this.name = activitySuggestion.getName();
        this.description = activitySuggestion.getDescription();
        this.region = activitySuggestion.getRegion();
        this.creationDate = activitySuggestion.getCreationDate();
        this.applicationDeadline = activitySuggestion.getApplicationDeadline();
        this.startingDate = activitySuggestion.getStartingDate();
        this.endingDate = activitySuggestion.getEndingDate();
        this.participantsNumberLimit = activitySuggestion.getParticipantsNumberLimit();
        this.state = activitySuggestion.getState();
        this.institutionId = activitySuggestion.getInstitution().getId();
        this.volunteerId = activitySuggestion.getVolunteer().getId();
    }

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public LocalDateTime getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDateTime startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }
}
package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

//import java.time.String;

public class ActivitySuggestionDto {
    private Integer id;
    private String name;
    private String description;
    private String region;
    private String creationDate;
    private String applicationDeadline;
    private String startingDate;
    private String endingDate;
    private Integer participantsNumberLimit;
    private String state;
    private Integer institutionId;
    private Integer volunteerId;

    public ActivitySuggestionDto() {}

    public ActivitySuggestionDto(ActivitySuggestion activitySuggestion) {
        this.id = activitySuggestion.getId();
        this.name = activitySuggestion.getName();
        this.description = activitySuggestion.getDescription();
        this.region = activitySuggestion.getRegion();
        this.creationDate = DateHandler.toISOString(activitySuggestion.getCreationDate());
        this.startingDate = DateHandler.toISOString(activitySuggestion.getStartingDate());
        this.endingDate = DateHandler.toISOString(activitySuggestion.getEndingDate());
        this.applicationDeadline = DateHandler.toISOString(activitySuggestion.getApplicationDeadline());
        this.participantsNumberLimit = activitySuggestion.getParticipantsNumberLimit();
        this.state = activitySuggestion.getState().name();
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
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
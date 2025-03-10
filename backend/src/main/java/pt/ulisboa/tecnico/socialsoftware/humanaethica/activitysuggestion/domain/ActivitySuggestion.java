package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "activity_suggestion")
public class ActivitySuggestion {

    public enum State {IN_REVIEW, APPROVED, REJECTED}

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private String description;
    private String region;
    private LocalDateTime creationDate;
    private LocalDateTime applicationDeadline;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private Integer participantsNumberLimit;

    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne
    @JoinColumn(name = "institution", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "volunteer", nullable = false)
    private Volunteer volunteer;

    public ActivitySuggestion() {}

    public ActivitySuggestion(Institution institution, Volunteer volunteer, ActivitySuggestionDto activitySuggestionDto) {
        setInstitution(institution);
        setVolunteer(volunteer);

        setName(activitySuggestionDto.getName());
        setDescription(activitySuggestionDto.getDescription());
        setRegion(activitySuggestionDto.getRegion());
        setApplicationDeadline(activitySuggestionDto.getApplicationDeadline());
        setStartingDate(activitySuggestionDto.getStartingDate());
        setEndingDate(activitySuggestionDto.getEndingDate());
        setParticipantsNumberLimit(activitySuggestionDto.getParticipantsNumberLimit());

        this.creationDate = LocalDateTime.now();
        this.state = State.IN_REVIEW;

        verifyInvariants();
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addActivitySuggestion(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addActivitySuggestion(this);
    }


    private void verifyInvariants() {
        descriptionHasMinimumLength();
        activitySuggestionByVolunteerIsUnique();
        deadlineDateIsAtLeast7DaysAfterCreationDate();
    }

    private void descriptionHasMinimumLength() {
        if (this.description == null || description.length() < 10) {
            throw new HEException(ACTIVITY_SUGGESTION_DESCRIPTION_TOO_SHORT);
        }
    }

    private void activitySuggestionByVolunteerIsUnique() {
        if (this.volunteer.getActivitySuggestion().stream()
                .anyMatch(activitySuggestion -> activitySuggestion != this && activitySuggestion.getName().equals(this.getName()))) {
            throw new HEException(ACTIVITY_SUGGESTION_ALREADY_MADE_BY_VOLUNTEER);
        }
    }

    private void deadlineDateIsAtLeast7DaysAfterCreationDate() {
        if (this.applicationDeadline.isBefore(this.creationDate.plusDays(7))) {
            throw new HEException(ACTIVITY_SUGGESTION_DEADLINE_TOO_SOON);
        }
    }

}
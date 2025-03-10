package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;


@Entity
@Table(name = "volunteerprofile")
public class VolunteerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String shortBio;

    private Integer numTotalEnrollments;

    private Integer numTotalParticipations;

    private Integer numTotalAssessments;

    private Double averageRating;


    @OneToMany(mappedBy = "volunteerProfile")
    private List<Participation> participations = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "volunteer_id", unique = true)
    private Volunteer volunteer;

    public VolunteerProfile() {
    }

    public VolunteerProfile(VolunteerProfileDto volunteerProfileDto, Volunteer volunteer) {
        setVolunteer(volunteer);
        setShortBio(volunteerProfileDto.getShortBio());
        setNumTotalEnrollments(volunteerProfileDto.getNumTotalEnrollments());
        setNumTotalParticipations(volunteerProfileDto.getNumTotalParticipations());
        setNumTotalAssessments(volunteerProfileDto.getNumTotalAssessments());
        setAverageRating(volunteerProfileDto.getAverageRating());

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public Integer getNumTotalEnrollments() {
        return numTotalEnrollments;
    }

    public void setNumTotalEnrollments(Integer numTotalEnrollments) {
        this.numTotalEnrollments = numTotalEnrollments;
    }

    public Integer getNumTotalParticipations() {
        return numTotalParticipations;
    }

    public void setNumTotalParticipations(Integer numTotalParticipations) {
        this.numTotalParticipations = numTotalParticipations;
    }

    public Integer getNumTotalAssessments() {
        return numTotalAssessments;
    }

    public void setNumTotalAssessments(Integer numTotalAssessments) {
        this.numTotalAssessments = numTotalAssessments;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }


    public List<Participation> getParticipations() {
        return participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

    public void deleteParticipation(Participation participation) {
        this.participations.remove(participation);
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    private void verifyInvariants() {
        shortBioHasMinimumLength();
    }

    private void shortBioHasMinimumLength() {
        if (shortBio == null || shortBio.trim().length() < 10) {
            throw new HEException(SHORT_BIO_TOO_SHORT);
        }
    }
}
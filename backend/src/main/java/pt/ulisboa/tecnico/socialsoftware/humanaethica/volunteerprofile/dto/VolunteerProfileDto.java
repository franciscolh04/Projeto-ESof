package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VolunteerProfileDto {
    private Integer id;
    private String shortBio;
    private Integer numTotalEnrollments;
    private Integer numTotalParticipations;
    private Integer numTotalAssessments;
    private Double averageRating;
    private List<Integer> selectedParticipationsIds;

    public VolunteerProfileDto() {
    }

    public VolunteerProfileDto(VolunteerProfile volunteerProfile) {
        setId(volunteerProfile.getId());
        setShortBio(volunteerProfile.getShortBio());
        setNumTotalEnrollments(volunteerProfile.getNumTotalEnrollments());
        setNumTotalParticipations(volunteerProfile.getNumTotalParticipations());
        setNumTotalAssessments(volunteerProfile.getNumTotalAssessments());
        setAverageRating(volunteerProfile.getAverageRating());
        setSelectedParticipationsIds(volunteerProfile.getSelectedParticipations().stream()
                .map(participation -> participation.getId())
                .collect(Collectors.toList()));
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setNumTotalEnrollments(Integer numTotalEnrollments) {
        this.numTotalEnrollments = numTotalEnrollments;
    }

    public Integer getNumTotalEnrollments() {
        return numTotalEnrollments;
    }

    public void setNumTotalParticipations(Integer numTotalParticipations) {
        this.numTotalParticipations = numTotalParticipations;
    }

    public Integer getNumTotalParticipations() {
        return numTotalParticipations;
    }

    public void setNumTotalAssessments(Integer numTotalAssessments) {
        this.numTotalAssessments = numTotalAssessments;
    }

    public Integer getNumTotalAssessments() {
        return numTotalAssessments;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public List<Integer> getSelectedParticipationsIds() {
        return selectedParticipationsIds;
    }

    public void setSelectedParticipationsIds(List<Integer> selectedParticipationsIds) {
        this.selectedParticipationsIds = selectedParticipationsIds;
    }

    @Override
    public String toString() {
        return "VolunteerProfileDto{" +
                "id=" + id +
                ", shortBio='" + shortBio + '\'' +
                ", numTotalEnrollments=" + numTotalEnrollments +
                ", numTotalParticipations=" + numTotalParticipations +
                ", numTotalAssessments=" + numTotalAssessments +
                ", averageRating=" + averageRating +
                '}';
    }
}

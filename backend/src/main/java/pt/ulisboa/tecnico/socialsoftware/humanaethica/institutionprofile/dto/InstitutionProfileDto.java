package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile;

import java.util.List;

public class InstitutionProfileDto {
    
    private String shortDescription;
    private Integer numMembers;
    private Integer numActivities;
    private Integer numAccessments;
    private Integer numVolunteers;
    private Double averageRating;
    // We won't need the institution object here, only its id
    private Integer institutionId;
    // In the case of assessments, we need the Date information,
    // so we need to use the AssessmentDto class
    private List<AssessmentDto> assessments;

    public InstitutionProfileDto() {}

    public InstitutionProfileDto(InstitutionProfile institutionProfile, boolean deepCopyAssessments) {
        this.shortDescription = institutionProfile.getShortDescription();
        this.numMembers = institutionProfile.getNumMembers();
        this.numActivities = institutionProfile.getNumActivities();
        this.numAccessments = institutionProfile.getNumAssessments();
        this.numVolunteers = institutionProfile.getNumVolunteers();
        this.averageRating = institutionProfile.getAverageRating();
        this.institutionId = institutionProfile.getInstitution().getId();

        if (deepCopyAssessments) {
            this.assessments = institutionProfile.getAssessments().stream()
                .map(assessment->new AssessmentDto(assessment))
                .toList();
        }
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getNumMembers() {
        return numMembers;
    }

    public void setNumMembers(Integer numMembers) {
        this.numMembers = numMembers;
    }

    public Integer getNumActivities() {
        return numActivities;
    }

    public void setNumActivities(Integer numActivities) {
        this.numActivities = numActivities;
    }

    public Integer getNumAccessments() {
        return numAccessments;
    }

    public void setNumAssessments(Integer numAccessments) {
        this.numAccessments = numAccessments;
    }

    public Integer getNumVolunteers() {
        return numVolunteers;
    }

    public void setNumVolunteers(Integer numVolunteers) {
        this.numVolunteers = numVolunteers;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getInstitution() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public List<AssessmentDto> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentDto> assessments) {
        this.assessments = assessments;
    }

}

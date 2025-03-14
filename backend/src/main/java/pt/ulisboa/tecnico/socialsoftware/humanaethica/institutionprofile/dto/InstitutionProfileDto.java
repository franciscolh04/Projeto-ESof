package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile;

import java.util.List;

public class InstitutionProfileDto {
    
    private String shortDescription;
    private Integer numMembers;
    private Integer numActivities;
    private Integer numAssessments;
    private Integer numVolunteers;
    private Double averageRating;
    // We won't need the institution object here, only its id
    private Integer institutionId;
    // In the case of assessments, we need the Date information,
    // so we need to use the AssessmentDto class
    private List<AssessmentDto> assessments;

    public InstitutionProfileDto() {}

    public InstitutionProfileDto(InstitutionProfile institutionProfile) {
        setShortDescription(institutionProfile.getShortDescription());
        setNumMembers(institutionProfile.getNumMembers());
        setNumActivities(institutionProfile.getNumActivities());
        setNumAssessments(institutionProfile.getNumAssessments());
        setNumVolunteers(institutionProfile.getNumVolunteers());
        setAverageRating(institutionProfile.getAverageRating());
        setInstitutionId(institutionProfile.getInstitution().getId());
        setAssessments(institutionProfile.getAssessments());
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

    public Integer getNumAssessments() {
        return numAssessments;
    }

    public void setNumAssessments(Integer numAssessments) {
        this.numAssessments = numAssessments;
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

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public List<AssessmentDto> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        if (assessments != null){
            this.assessments = assessments.stream()
                .map(assessment->new AssessmentDto(assessment))
                .toList();
            setNumAssessments(assessments.size());
        }
    }

}

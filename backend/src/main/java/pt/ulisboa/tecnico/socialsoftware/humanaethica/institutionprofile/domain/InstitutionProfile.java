package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "institution_profile")
public class InstitutionProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String shortDescription;
    private Integer numMembers;
    private Integer numActivities;
    private Integer numAssessments;
    private Integer numVolunteers;
    private Double averageRating;

    @OneToOne
    private Institution institution;

    @OneToMany(mappedBy = "institution_profile", fetch = FetchType.LAZY )
    private List<Assessment> assessments = new ArrayList<>();

    public InstitutionProfile() {}

    public InstitutionProfile(Institution institution, InstitutionProfileDto institutionProfileDto) {
        setInstitution(institution);
        setNumMembers(institution.getMembers.size());
        setNumActivities(institution.getActivities.size());
        setNumAssessments(institution.getAssessments.size());

        setShortDescription(institutionProfileDto.getShortDescription());
        setNumVolunteers(institutionProfileDto.getNumVolunteers());
        setAverageRating(institutionProfileDto.getAverageRating());
        
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.setInstitutonProfile(this);
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }

}
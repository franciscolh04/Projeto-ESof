package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import jakarta.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Optional;

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
    @JoinColumn(name = "institution_id",unique=true)
    private Institution institution;

    @OneToMany(mappedBy = "institutionProfile", fetch = FetchType.LAZY )
    private List<Assessment> assessments = new ArrayList<>();

    public InstitutionProfile() {}

    public InstitutionProfile(Institution institution, InstitutionProfileDto institutionProfileDto) {
        setInstitution(institution);

        updateInstitutionProfile();

        updateAssementsInstitutionProfile();

        if(institution.getActivities() != null){
            updateAverageRating();
        }
    
        setShortDescription(institutionProfileDto.getShortDescription());

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
        verifyInstitutionDescription();
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
        institution.setInstitutionProfile(this);
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
        updateAssementsInstitutionProfile();
        setNumAssessments(assessments.size());
        verifyInvariants();
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
        assessment.setInstitutionProfile(this);
        setNumAssessments(assessments.size());
        verifyInvariants();
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
        setNumAssessments(assessments.size());
        verifyInvariants();
    }

    public void updateAssementsInstitutionProfile() {
        // ensure that the institution profile is set for each assessment
        // even if they were crated before the institution profile
        for (Assessment assessment : assessments) {
            assessment.setInstitutionProfile(this);
        }
    }

    public void updateInstitutionProfile() {
        setNumMembers(institution.getMembers() != null ? institution.getMembers().size() : 0);
        setNumActivities(institution.getActivities() != null ? institution.getActivities().size() : 0);
        setNumAssessments(institution.getAssessments() != null ? institution.getAssessments().size() : 0);
        if(institution.getAssessments() != null){updateAssements();}
        if(institution.getActivities() != null){updateAverageRating();}
    }


    public void updateAssements() {

        // TODO: set a maximum number of assessments to be displayed based
        // on user input
        assessments = new ArrayList<>(institution.getAssessments());

    }

    public void updateNumVolunteers() {
        
        numVolunteers = institution.getActivities().stream()
        .mapToInt(Activity::getNumberOfParticipatingVolunteers).sum();

    }

    public void updateAverageRating() {
        updateNumVolunteers();
    
        double totalRating = institution.getActivities()
            .stream()
            .flatMap(activity -> Optional.ofNullable(activity.getParticipations())
                                         .orElse(Collections.emptyList())
                                         .stream()) 
            .mapToDouble(Participation::getVolunteerRating)
            .sum();
    
        averageRating = numVolunteers > 0 ? totalRating / numVolunteers : 0.0;
    }
    

    public void verifyInvariants(){
        verifyInstitutionDescription();
        verifyAssessmentSelection();
        verifyRecentAssessments();
    }

    public void verifyInstitutionDescription() {
        if (shortDescription == null) {
            throw new HEException(INVALID_SHORT_DESCRIPTION);
        } else if (shortDescription.trim().length() < 10) {
            throw new HEException(INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT,shortDescription.trim().length());
        }
    }

    public void verifyAssessmentSelection() {
        if (institution.getAssessments() == null){
            return;
        }
        if (this.numAssessments < institution.getAssessments().size() * 0.5) {
            throw new HEException(INSTITUTION_SELECTED_ASSESSMENTS);
        }
    }

    private void verifyRecentAssessments() {
        if (this.assessments.isEmpty() || this.institution == null || this.institution.getAssessments().isEmpty()) {
            return;
        }
        
        int minRecentAssessments = (int) Math.ceil(getAssessments().size() * 0.2);
    
        List<Assessment> mostRecentInstitutionAssessments = getInstitution().getAssessments().stream()
                .sorted((a1, a2) -> a2.getReviewDate().compareTo(a1.getReviewDate()))
                .limit(minRecentAssessments)
                .toList();
    
        long recentAssessmentsInList = this.assessments.stream()
                .filter(mostRecentInstitutionAssessments::contains)
                .count();
    
        if (recentAssessmentsInList < minRecentAssessments) {
            throw new HEException(INSTITUTION_MOST_RECENT_ASSESSMENTS);
        }
    }


}
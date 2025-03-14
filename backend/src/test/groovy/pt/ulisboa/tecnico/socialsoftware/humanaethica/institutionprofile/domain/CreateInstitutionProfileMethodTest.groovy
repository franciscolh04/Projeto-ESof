package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation


import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileMethodTest extends SpockTest {
    Institution institution = Mock()
    Member member = Mock()
    Assessment assessment = Mock()
    Assessment assessment1 = Mock()
    Assessment assessment2 = Mock()
    Assessment assessment3 = Mock()
    Assessment assessment4 = Mock()
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Volunteer volunteer = Mock()
    Participation participation = Mock()
    Participation otherParticipation = Mock()
    def institutionProfileDto

    def setup() {
        given: "institution profile info"
        institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = SHORTDESCRIPTION
    }

    def "create institution profile"() {
        given:

        participation.getVolunteerRating() >> 5
        otherParticipation.getVolunteerRating() >> 3

        activity.getNumberOfParticipatingVolunteers() >> 3
        otherActivity.getNumberOfParticipatingVolunteers() >> 5
        activity.getParticipations() >> [participation]
        otherActivity.getParticipations() >> [otherParticipation]
        assessment1.getReviewDate() >> TWO_DAYS_AGO
        assessment2.getReviewDate() >> TWO_DAYS_AGO

        institution.getActivities() >> [activity, otherActivity]
        institution.getMembers() >> [member]

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.setAssessments([assessment1])
        result.removeAssessment(assessment1)
        result.addAssessment(assessment1)

        then: "checks results"
        result.shortDescription == SHORTDESCRIPTION
        result.numMembers == 1
        result.numActivities == 2
        result.numAssessments == 1
        result.numVolunteers == 8
        result.averageRating == 1 // (5+3)->volunteerRating / (3+5)->numVolunteers
        result.institution == institution

        and: "check that it is added"
        1 * institution.setInstitutionProfile(_)
        2 * assessment1.setInstitutionProfile(_)
    }

    def "create institution profile to improve coverage"() {
        given: 
        assessment1.getReviewDate() >> ONE_DAY_AGO
        institution.getId() >> institution.id
        institution.getName() >> INSTITUTION_1_NAME

        participation.getVolunteerRating() >> 5
        otherParticipation.getVolunteerRating() >> 3

        activity.getNumberOfParticipatingVolunteers() >> 3
        otherActivity.getNumberOfParticipatingVolunteers() >> 5
        activity.getParticipations() >> [participation]
        otherActivity.getParticipations() >> [otherParticipation]

        institution.getActivities() >> [activity, otherActivity]
        institution.getMembers() >> [member]

        institution.getAssessments() >> [assessment1]
        activity.getEndingDate() >> ONE_DAY_AGO
        assessment1.getVolunteer() >> volunteer
        assessment1.getId() >> 1;
        assessment1.getInstitution()>>institution
        institution.getId() >> institution.id
        institution.getName() >> INSTITUTION_1_NAME;

        volunteer.getId() >> 2
        volunteer.getName() >> INSTITUTION_1_NAME;
        assessment1.getReview() >> VOLUNTEER_REVIEW;
        assessment1.getReviewDate() >> TWO_DAYS_AGO;
        def institutionProfile = new InstitutionProfile(institution, institutionProfileDto)
        
        when:
        def result = new InstitutionProfileDto(institutionProfile)
        result.setAssessments(null)
        result.setAssessments([assessment1])

        then: "checks results"
        result.getNumMembers() == 1
        result.getNumActivities() == 2
        result.getNumAssessments() == 1
        result.getNumVolunteers() == 8
        result.getAverageRating() == 1
        result.getAssessments().size() == 1
        result.getInstitutionId() == institution.id
    }

    @Unroll
    def "create institution profile and violate short description : description=#description"() {

        given:
        participation.getVolunteerRating() >> 5

        activity.getNumberOfParticipatingVolunteers() >> 3
        activity.getParticipations() >> [participation]

        institution.getActivities() >> [activity]
        institution.getMembers() >> [member]
        institution.getAssessments() >> []
        and:
        institutionProfileDto.shortDescription = description

        when:
        new InstitutionProfile(institution, institutionProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        description       || errorMessage
        null              || ErrorMessage.INVALID_SHORT_DESCRIPTION
        "  "              || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT
        "   1111111 1   " || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT
        "  12345,  8"     || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT
    }

    def "create institution profile and violate 50% of selected assessments invariant with 1 element"() {

        given:
        participation.getVolunteerRating() >> 5

        activity.getNumberOfParticipatingVolunteers() >> 3
        activity.getParticipations() >> [participation]

        institution.getMembers() >> [member]
        assessment1.getReviewDate() >> TWO_DAYS_AGO
        institution.getAssessments() >> [assessment1]
        and:
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.removeAssessment(assessment1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_SELECTED_ASSESSMENTS
    }

    def "create institution profile and violate 50% of selected assessments invariant with more than 1 element"() {

        given:
        participation.getVolunteerRating() >> 5

        activity.getParticipations() >> [participation]

        institution.getActivities() >> [activity]
        assessment1.getReviewDate() >> TWO_DAYS_AGO
        assessment.getReviewDate() >> ONE_DAY_AGO
        assessment2.getReviewDate() >> THREE_DAYS_AGO
        institution.getAssessments() >> [assessment1,assessment,assessment2]
        and:
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.setAssessments([assessment])

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_SELECTED_ASSESSMENTS
    }

    def "create institution profile and violate most recent 20% selected activities"() {
        given:
        assessment.getReviewDate() >> THREE_DAYS_AGO
        assessment1.getReviewDate() >> TWO_DAYS_AGO
        assessment2.getReviewDate() >> ONE_DAY_AGO
        assessment3.getReviewDate() >> NOW
        assessment4.getReviewDate() >> IN_ONE_DAY

        participation.getVolunteerRating() >> 5

        activity.getNumberOfParticipatingVolunteers() >> 3
        activity.getParticipations() >> [participation]

        institution.getActivities() >> [activity]
        institution.getMembers() >> [member]
        institution.getAssessments() >> [assessment1, assessment2, assessment3, assessment4]

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.setAssessments([assessment,assessment1])

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_MOST_RECENT_ASSESSMENTS
    }
    
    def "create institution profile and violate most recent 20% selected activities edge case"() {
        given:
        assessment.getReviewDate() >> THREE_DAYS_AGO
        assessment4.getReviewDate() >> IN_ONE_DAY

        participation.getVolunteerRating() >> 5

        institution.getMembers() >> [member]
        institution.getAssessments() >> [assessment4]

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.addAssessment(assessment)
        result.removeAssessment(assessment4)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_MOST_RECENT_ASSESSMENTS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
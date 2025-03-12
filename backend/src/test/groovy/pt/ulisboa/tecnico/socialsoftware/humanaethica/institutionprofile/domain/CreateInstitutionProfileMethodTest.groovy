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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation


import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileMethodTest extends SpockTest {
    Institution institution = Mock()
    Member member = Mock()
    Assessment assessment = Mock()
    Assessment assessment1 = Mock()
    Assessment assessment2 = Mock()
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Participation participation = Mock()
    Participation otherParticipation = Mock()
    def institutionProfileDto

    def setup() {
        given: "institution profile info"
        institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = "short description"
    }

    def "create institution profile"() {
        given:

        participation.getVolunteerRating() >> 5
        otherParticipation.getVolunteerRating() >> 3

        activity.getNumberOfParticipatingVolunteers() >> 3
        otherActivity.getNumberOfParticipatingVolunteers() >> 5
        activity.getParticipations() >> [participation]
        otherActivity.getParticipations() >> [otherParticipation]

        institution.getActivities() >> [activity, otherActivity]
        institution.getMembers() >> [member]
        institution.getAssessments() >> [assessment]

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)

        then: "checks results"
        result.shortDescription == "short description"
        result.numMembers == 1
        result.numActivities == 2
        result.numAssessments == 1
        result.numVolunteers == 8
        result.averageRating == 1 // (5+3)->volunteerRating / (3+5)->numVolunteers

        result.institution == institution

        and: "check that it is added"
        1 * institution.setInstitutionProfile(_)
        1 * assessment.setInstitutionProfile(_)
    }

    @Unroll
    def "create institution profile and violate short description : description=#description"() {

        given:
        participation.getVolunteerRating() >> 5

        activity.getNumberOfParticipatingVolunteers() >> 3
        activity.getParticipations() >> [participation]

        institution.getActivities() >> [activity]
        institution.getMembers() >> [member]
        institution.getAssessments() >> [assessment]
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

        institution.getActivities() >> [activity]
        institution.getMembers() >> [member]
        assessment1.getReviewDate() >> TWO_DAYS_AGO
        institution.getAssessments() >> [assessment1]
        and:
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)
        result.setAssessments([])

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_SELECTED_ASSESSMENTS
    }

    def "create institution profile and violate 50% of selected assessments invariant with more than 1 element"() {

        given:
        participation.getVolunteerRating() >> 5

        activity.getNumberOfParticipatingVolunteers() >> 3
        activity.getParticipations() >> [participation]

        institution.getActivities() >> [activity]
        institution.getMembers() >> [member]
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

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
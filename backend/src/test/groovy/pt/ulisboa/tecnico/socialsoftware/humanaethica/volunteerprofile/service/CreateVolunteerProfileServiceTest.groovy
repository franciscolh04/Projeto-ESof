package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class CreateVolunteerProfileServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    @Shared
    def volunteer
    @Shared
    def participations

    def setup() {
        def authUser = authUserService.loginDemoVolunteerAuth()
        volunteer = userRepository.findById(authUser.getUser().getId()).orElseThrow()

        participations = []
        for (int i = 1; i <= 4; i++) {
            def part = new Participation()
            part.volunteer = volunteer
            part.memberReview = MEMBER_REVIEW
            participationRepository.save(part)
            participations.add(part)
        }
    }

    def 'create volunteerProfile as volunteer' () {
        given:
        def volunteerProfileDto = createVolunteerProfileDto(VOLUNTEER_PROFILE_SHORT_BIO_VALID, 
            VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID, VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID,
            VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID, VOLUNTEER_PROFILE_AVERAGE_RATING_VALID, participations*.id)

        when:
        def result = volunteerProfileService.createVolunteerProfile(volunteer.id, volunteerProfileDto)

        then: "the volunteer profile is stored in the database"
        result.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        result.numTotalEnrollments == VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID
        result.numTotalParticipations == VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID
        result.numTotalAssessments == VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID
        result.averageRating == VOLUNTEER_PROFILE_AVERAGE_RATING_VALID
        result.selectedParticipationsIds.size() == 4
        and: "the stored data is correct"
        volunteerProfileRepository.findAll().size() == 1
        def storedVolunteerProfile = volunteerProfileRepository.findAll().get(0)
        storedVolunteerProfile.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        storedVolunteerProfile.numTotalEnrollments == VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID
        storedVolunteerProfile.numTotalParticipations == VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID
        storedVolunteerProfile.numTotalAssessments == VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID
        storedVolunteerProfile.averageRating == VOLUNTEER_PROFILE_AVERAGE_RATING_VALID
        storedVolunteerProfile.selectedParticipations.size() == 4
    }

    
    @Unroll
    def 'invalid arguments: userId=#userId | errorMessage=#errorMessage'() {
        given:
        def volunteerProfileDto = createVolunteerProfileDto(VOLUNTEER_PROFILE_SHORT_BIO_VALID,
            VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID, VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID,
            VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID, VOLUNTEER_PROFILE_AVERAGE_RATING_VALID, participations*.id)


        if (userId == EXIST && volunteer.getProfile() == null) {
            def existingProfile = new VolunteerProfile()
            volunteer.setProfile(existingProfile)
            volunteerProfileRepository.save(existingProfile)
        }

        when:
        volunteerProfileService.createVolunteerProfile(getVolunteerId(userId), volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        userId       || errorMessage
        null         || ErrorMessage.USER_NOT_FOUND
        NO_EXIST     || ErrorMessage.USER_NOT_FOUND
        EXIST        || ErrorMessage.VOLUNTEER_PROFILE_ALREADY_EXISTS
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 333
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
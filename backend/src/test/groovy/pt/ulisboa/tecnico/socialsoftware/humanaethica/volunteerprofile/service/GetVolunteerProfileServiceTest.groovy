package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

import spock.lang.Unroll

@DataJpaTest
class GetVolunteerProfileServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer

    def setup() {
        volunteer = new Volunteer()
        userRepository.save(volunteer)
    }

    def 'get volunteerProfile' () {
        given:
        def volunteerProfile = new VolunteerProfile()
        volunteerProfile.volunteer = volunteer
        volunteer.volunteerProfile = volunteerProfile
        volunteerProfileRepository.save(volunteerProfile)

        when:
        def result = volunteerProfileService.getVolunteerProfile(volunteer.id)

        then: "the volunteer profile is returned"
        result.id == volunteerProfile.id
    }

    @Unroll
    def 'invalid arguments: userId=#userId | errorMessage=#errorMessage'() {
        when:
        volunteerProfileService.getVolunteerProfile(getVolunteerId(userId))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        userId       || errorMessage
        null         || ErrorMessage.USER_NOT_FOUND
        NO_EXIST     || ErrorMessage.USER_NOT_FOUND
        EXIST        || ErrorMessage.VOLUNTEER_PROFILE_NOT_FOUND
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
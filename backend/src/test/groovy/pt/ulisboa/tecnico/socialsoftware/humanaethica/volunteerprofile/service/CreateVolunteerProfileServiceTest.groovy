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

@DataJpaTest
class CreateVolunteerProfileServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer
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
        def volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.shortBio = VOLUNTEER_PROFILE_SHORT_BIO_VALID
        volunteerProfileDto.numTotalEnrollments = 10
        volunteerProfileDto.numTotalParticipations = 6
        volunteerProfileDto.numTotalAssessments = 4
        volunteerProfileDto.averageRating = 3
        volunteerProfileDto.selectedParticipationsIds = participations*.id

        when:
        def result = volunteerProfileService.createVolunteerProfile(volunteer.id, volunteerProfileDto)

        then: "the volunteer profile is stored in the database"
        result.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        result.numTotalEnrollments == 10
        result.numTotalParticipations == 6
        result.numTotalAssessments == 4
        result.averageRating == 3
        result.selectedParticipationsIds.size() == 4
        and: "the stored data is correct"
        volunteerProfileRepository.findAll().size() == 1
        def storedVolunteerProfile = volunteerProfileRepository.findAll().get(0)
        storedVolunteerProfile.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        storedVolunteerProfile.numTotalEnrollments == 10
        storedVolunteerProfile.numTotalParticipations == 6
        storedVolunteerProfile.numTotalAssessments == 4
        storedVolunteerProfile.averageRating == 3
        storedVolunteerProfile.selectedParticipations.size() == 4
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
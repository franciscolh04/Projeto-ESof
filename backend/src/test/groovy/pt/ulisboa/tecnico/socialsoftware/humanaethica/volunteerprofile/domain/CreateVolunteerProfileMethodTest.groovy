package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import spock.lang.Unroll

@DataJpaTest
class CreateVolunteerProfileMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Participation participation1 = Mock()
    Participation participation2 = Mock()
    Participation participation3 = Mock()

    @Unroll
    def "create volunteer profile and violate shortBio minimum length invariant: #shortBio"() {
        given: "an invalid shortBio"
        VolunteerProfileDto volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.setShortBio(shortBio)

        when:
        new VolunteerProfile(volunteerProfileDto, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        shortBio                 || errorMessage
        null                     || ErrorMessage.SHORT_BIO_TOO_SHORT
        " "                      || ErrorMessage.SHORT_BIO_TOO_SHORT
        "Too short"              || ErrorMessage.SHORT_BIO_TOO_SHORT
        "      Trim      "       || ErrorMessage.SHORT_BIO_TOO_SHORT
    }

    @Unroll
    def "create volunteer profile with valid shortBio: #shortBio"() {
        given: "a valid shortBio"
        VolunteerProfileDto volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.setShortBio(shortBio)

        when:
        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteerProfileDto, volunteer)

        then: "no exception should be thrown and the shortBio should be set correctly"
        volunteerProfile.getShortBio() == shortBio

        where:
        shortBio << [VOLUNTEER_PROFILE_SHORT_BIO_VALID, "Sebastiana", "Valid                  bio", "B        i" ]

    }

    @Unroll
    def "create volunteer profile with selected participations assessed: memberRating1=#memberRating1 | memberRating2=#memberRating2 | memberRating3=#memberRating3"() {
        given: "a volunteer profile with selected participations"
        VolunteerProfileDto volunteerProfileDto = new VolunteerProfileDto()
        participation1.getMemberRating() >> memberRating1
        participation2.getMemberRating() >> memberRating2
        participation3.getMemberRating() >> memberRating3
        volunteerProfileDto.setSelectedParticipations([participation1, participation2, participation3])
        volunteerProfileDto.setShortBio(VOLUNTEER_PROFILE_SHORT_BIO_VALID)

        when:
        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteerProfileDto, volunteer)

        then: "no exception should be thrown"
        volunteerProfile.getSelectedParticipations() == [participation1, participation2, participation3]

        where:
        memberRating1  | memberRating2  |  memberRating3
        1              | 1              | 1
        3              | 4              | 5
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
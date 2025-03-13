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
    Participation part1 = Mock()
    Participation part2 = Mock()
    Participation part3 = Mock()
    Participation part4 = Mock()
    Participation part5 = Mock()

    def volunteerProfileDto

    def setup() {
        given: "volunteerProfileDto"
        volunteerProfileDto = new VolunteerProfileDto()
        volunteer.getParticipations() >> [part1, part2, part3, part4, part5]
    }

    @Unroll
    def "create a valid volunteer profile: review1=#review1 | review2=#review2 | review3=#review3 | avgRating=#avgRating | list=#list | totalEnr=#totalEnr | totalPart=#totalPart | totalAss=#totalAss | shortBio=#shortBio"() {
        given: "a valid volunteer"
        part1.getId() >> 1
        part2.getId() >> 2
        part3.getId() >> 3
        part1.getMemberReview() >> review1
        part2.getMemberReview() >> review2
        part3.getMemberReview() >> review3
        volunteerProfileDto.setShortBio(shortBio)
        volunteerProfileDto.setNumTotalEnrollments(totalEnr)
        volunteerProfileDto.setNumTotalParticipations(totalPart)
        volunteerProfileDto.setNumTotalAssessments(totalAss)
        volunteerProfileDto.setAverageRating(avgRating)
        volunteerProfileDto.setSelectedParticipationsIds(list)


        when:
        def result = new VolunteerProfile(volunteer, volunteerProfileDto)

        then: "check result"
        result.getShortBio() == shortBio
        result.getNumTotalEnrollments() == totalEnr
        result.getNumTotalParticipations() == totalPart
        result.getNumTotalAssessments() == totalAss
        result.getAverageRating() == avgRating
        result.getSelectedParticipations().collect { it.getId() } == list
        result.getVolunteer() == volunteer



        where:
        review1 | review2 | review3 | avgRating | list      | totalEnr | totalPart | totalAss | shortBio
        "bom"   | "bom"   | "bom"   | 1         | [1, 2, 3] | 7        | 5         | 3        | "Valid        bio"
        "mau"   | "mau"   | null    | 3.5       | [1,2]     | 5        | 5         | 2        | "B            i"
        null    | null    | null    | null      | []        | 5        | 5         | 0        | VOLUNTEER_PROFILE_SHORT_BIO_VALID
        "bom"   | "bom"   | "bom"   | null      | [1, 2, 3] | 7        | null      | 3        | "Valid        bio"
        "bom"   | "bom"   | "bom"   | 1         | []        | 7        | 5         | null     | "Valid        bio"
        "bom"   | "bom"   | "bom"   | 1         | []        | 7        | null      | null     | "Valid        bio"





    }

    @Unroll
    def "create volunteer profile and violate shortBio minimum length invariant: #shortBio"() {
        given: "an invalid shortBio"
        volunteerProfileDto.setShortBio(shortBio)

        when:
        new VolunteerProfile(volunteer, volunteerProfileDto)

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
    def "create volunteer profile and violate selected participations assessed invariant: review1=#review1 | review2=#review2 | review3=#review3"() {
        given: "a volunteer profile with selected participations not assessed"
        part1.getId() >> 1
        part2.getId() >> 2
        part3.getId() >> 3
        part1.getMemberReview() >> review1
        part2.getMemberReview() >> review2
        part3.getMemberReview() >> review3
        volunteerProfileDto.setSelectedParticipationsIds([1,2,3])
        volunteerProfileDto.setShortBio(VOLUNTEER_PROFILE_SHORT_BIO_VALID)

        when:
        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        review1        | review2        | review3        || errorMessage
        null           | "muito bom"    | "muito bom"    || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        "muito bom"    | null           | "muito bom"    || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        "muito bom"    | "muito bom"    | null           || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        null           | null           | null           || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
    }

    @Unroll
    def "create volunteer profile and violate the minimum selected participations invariant: totalParticipations=#totalParticipations | totalAssessments=#totalAssessments"() {
        given: "a volunteer profile with an invalid number of selected participations"
        part1.getId() >> 1
        part2.getId() >> 2
        part1.getMemberReview() >> VALID_VOLUNTEER_REVIEW
        part2.getMemberReview() >> VALID_VOLUNTEER_REVIEW
        volunteerProfileDto.setSelectedParticipationsIds([1, 2])
        volunteerProfileDto.setNumTotalParticipations(totalParticipations)
        volunteerProfileDto.setNumTotalAssessments(totalAssessments)
        volunteerProfileDto.setShortBio(VOLUNTEER_PROFILE_SHORT_BIO_VALID)


        when:
        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage


        where:
        totalParticipations  | totalAssessments || errorMessage
        6                    | 7                || ErrorMessage.SELECTED_PARTICIPATIONS_INVALID_NUMBER
        8                    | 3                || ErrorMessage.SELECTED_PARTICIPATIONS_INVALID_NUMBER
        6                    | 3                || ErrorMessage.SELECTED_PARTICIPATIONS_INVALID_NUMBER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
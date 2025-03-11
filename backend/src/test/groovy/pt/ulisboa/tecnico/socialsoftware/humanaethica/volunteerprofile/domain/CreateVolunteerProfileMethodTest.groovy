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
    def volunteerProfileDto

    def setup() {
        given: "volunteerProfileDto"
        volunteerProfileDto = new VolunteerProfileDto()
    }

    def createList(Participation part1, Participation part2, Participation part3,
                   boolean includePart1, boolean includePart2, boolean includePart3) {
        def list = []
        if (includePart1) list << part1
        if (includePart2) list << part2
        if (includePart3) list << part3
        return list
    }

    @Unroll
    def "create a valid volunteer profile: rating1=#rating1 | rating2=#rating2 | rating3=#rating3 | avgRating=#avgRating | includePart1=#includePart1 | includePart2=#includePart2 | includePart3=#includePart3 | totalEnr=#totalEnr | totalPart=#totalPart | totalAss=#totalAss | shortBio=#shortBio"() {
        given: "a valid volunteer"
        part1.getMemberRating() >> rating1
        part2.getMemberRating() >> rating2
        part3.getMemberRating() >> rating3
        def list = createList(part1, part2, part3, includePart1, includePart2, includePart3)
        volunteerProfileDto.setShortBio(shortBio)
        volunteerProfileDto.setNumTotalEnrollments(totalEnr)
        volunteerProfileDto.setNumTotalParticipations(totalPart)
        volunteerProfileDto.setNumTotalAssessments(totalAss)
        volunteerProfileDto.setAverageRating(avgRating)
        volunteerProfileDto.setSelectedParticipations(list)

        when:
        def result = new VolunteerProfile(volunteer, volunteerProfileDto)

        then: "check result"
        result.getShortBio() == shortBio
        result.getNumTotalEnrollments() == totalEnr
        result.getNumTotalParticipations() == totalPart
        result.getNumTotalAssessments() == totalAss
        result.getAverageRating() == avgRating
        result.getSelectedParticipations() == list


        where:
        rating1 | rating2 | rating3 | avgRating | includePart1 | includePart2 | includePart3 | totalEnr | totalPart | totalAss | shortBio
        1       | 1       | 1       | 1         | true         | true         | true         | 7        | 6         | 4        | "Valid        bio"
        3       | 4       | null    | 3.5       | true         | true         | false        | 4        | 4         | 2        | "B            i"
        null    | null    | null    | null      | false        | false        | false        | 0        | 0         | 0        | VOLUNTEER_PROFILE_SHORT_BIO_VALID

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
    def "create volunteer profile and violate selected participations assessed invariant: memberRating1=#memberRating1 | memberRating2=#memberRating2 | memberRating3=#memberRating3"() {
        given: "a volunteer profile with selected participations not assessed"
        part1.getMemberRating() >> memberRating1
        part2.getMemberRating() >> memberRating2
        part3.getMemberRating() >> memberRating3
        volunteerProfileDto.setSelectedParticipations([part1, part2, part3])
        volunteerProfileDto.setShortBio(VOLUNTEER_PROFILE_SHORT_BIO_VALID)

        when:
        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        memberRating1  | memberRating2  | memberRating3  || errorMessage
        null           | 1              | 1              || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        3              | null           | 5              || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        1              | 4              | null           || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
        null           | null           | null           || ErrorMessage.SELECTED_PARTICIPATION_NOT_ASSESSED
    }

    @Unroll
    def "create volunteer profile and violate the minimum selected participations invariant: totalParticipations=#totalParticipations | totalAssessments=#totalAssessments"() {
        given: "a volunteer profile with an invalid number of selected participations"
        part1.getMemberRating() >> VALID_PARTICIPATION_MEMBER_RATING
        part2.getMemberRating() >> VALID_PARTICIPATION_MEMBER_RATING
        volunteerProfileDto.setSelectedParticipations([part1, part2])
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
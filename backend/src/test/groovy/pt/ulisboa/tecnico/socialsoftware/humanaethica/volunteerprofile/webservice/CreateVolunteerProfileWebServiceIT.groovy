package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateVolunteerProfileWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def volunteer
    def volunteerProfileDto
    def participations

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoVolunteerLogin()
        volunteer = userRepository.findById(user.getId()).orElseThrow()

        participations = []
        for (int i = 1; i <= 4; i++) {
            def part = new Participation()
            part.volunteer = volunteer
            part.memberReview = MEMBER_REVIEW
            participationRepository.save(part)
            participations.add(part)
        }
        volunteer.setParticipations(participations)


        volunteerProfileDto = createVolunteerProfileDto(VOLUNTEER_PROFILE_SHORT_BIO_VALID,
                VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID, VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID,
                VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID, VOLUNTEER_PROFILE_AVERAGE_RATING_VALID, participations*.id)
    }


    def "volunteer creates volunteer profile"() {
        given:
        //demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/volunteers/' + volunteer.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(volunteerProfileDto)
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "check response data"
        response.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        response.numTotalEnrollments == VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID
        response.numTotalParticipations == VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID
        response.numTotalAssessments == VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID
        response.averageRating == VOLUNTEER_PROFILE_AVERAGE_RATING_VALID
        response.selectedParticipationsIds.size() == 4
        volunteerProfileRepository.findAll().size() == 1
        def volunteerProfile = volunteerProfileRepository.findAll().get(0)
        volunteerProfile.shortBio == VOLUNTEER_PROFILE_SHORT_BIO_VALID
        volunteerProfile.numTotalEnrollments == VOLUNTEER_PROFILE_NUM_TOTAL_ENROLLMENTS_VALID
        volunteerProfile.numTotalParticipations == VOLUNTEER_PROFILE_NUM_TOTAL_PARTICIPATIONS_VALID
        volunteerProfile.numTotalAssessments == VOLUNTEER_PROFILE_NUM_TOTAL_ASSESSMENTS_VALID
        volunteerProfile.averageRating == VOLUNTEER_PROFILE_AVERAGE_RATING_VALID
        //volunteerProfile.selectedParticipations.size() == 4
    }

    def "member tries to create volunteer profile"() {
        given:
        def member = demoMemberLogin()

        when:
        webClient.post()
                .uri('/volunteers/' + member.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(volunteerProfileDto)
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        volunteerProfileRepository.findAll().size() == 0
    }

    def "admin tries to create volunteer profile"() {
        given:
        def admin = demoAdminLogin()

        when:
        webClient.post()
                .uri('/volunteers/' + admin.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(volunteerProfileDto)
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        volunteerProfileRepository.findAll().size() == 0
    }
}
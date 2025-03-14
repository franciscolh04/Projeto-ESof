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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetVolunteerProfileWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def volunteer
    def volunteerProfile

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        volunteer = new Volunteer()
        userRepository.save(volunteer)
        volunteerProfile = new VolunteerProfile()
        volunteerProfile.volunteer = volunteer
        volunteer.volunteerProfile = volunteerProfile
        volunteerProfileRepository.save(volunteerProfile)
    }

    def 'get volunteerProfile as a volunteer' () {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.get()
                .uri('/volunteers/' + volunteer.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "the volunteer profile is returned"
        response.id == volunteerProfile.id
    }

    def 'get volunteerProfile as a member' () {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/volunteers/' + volunteer.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "the volunteer profile is returned"
        response.id == volunteerProfile.id
    }

    def 'get volunteerProfile as an admin' () {
        given:
        demoAdminLogin()

        when:
        def response = webClient.get()
                .uri('/volunteers/' + volunteer.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "the volunteer profile is returned"
        response.id == volunteerProfile.id
    }

    def 'get volunteerProfile as an unauthenticated user' () {
        when:
        def response = webClient.get()
                .uri('/volunteers/' + volunteer.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "the volunteer profile is returned"
        response.id == volunteerProfile.id
    }
}
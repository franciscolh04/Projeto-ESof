package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivitySuggestionsByInstitutionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution
    def activitySuggestionDto
    def activitySuggestionDto2
    def activitySuggestion1
    def activitySuggestion2

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 5, ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS, IN_NINE_DAYS, IN_TEN_DAYS)

        activitySuggestionDto2 = createActivitySuggestionDto(ACTIVITY_NAME_2, ACTIVITY_REGION_2, 10, ACTIVITY_DESCRIPTION_2,
                IN_NINE_DAYS, IN_TEN_DAYS, IN_ELEVEN_DAYS)

        // Create two activity suggestions
        activitySuggestion1 = activitySuggestionService.createActivitySuggestion(
                volunteer.id, institution.id, activitySuggestionDto)

        activitySuggestion2 = activitySuggestionService.createActivitySuggestion(
                volunteer.id, institution.id, activitySuggestionDto2)
    }

    def "login as a member, and get the activity suggestions list successfully"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri("/activitySuggestions/" + institution.id)
                .headers { httpHeaders -> httpHeaders.putAll(headers) }
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "check response data"
        response.size() == 2

        with(response.get(0)) {
            name == ACTIVITY_NAME_1
            region == ACTIVITY_REGION_1
            participantsNumberLimit == 5
            description == ACTIVITY_DESCRIPTION_1
            DateHandler.toLocalDateTime(startingDate).withNano(0) == IN_NINE_DAYS.withNano(0)
            DateHandler.toLocalDateTime(endingDate).withNano(0) == IN_TEN_DAYS.withNano(0)
            DateHandler.toLocalDateTime(applicationDeadline).withNano(0) == IN_EIGHT_DAYS.withNano(0)
        }

        with(response.get(1)) {
            name == ACTIVITY_NAME_2
            region == ACTIVITY_REGION_2
            participantsNumberLimit == 10
            description == ACTIVITY_DESCRIPTION_2
            DateHandler.toLocalDateTime(startingDate).withNano(0) == IN_TEN_DAYS.withNano(0)
            DateHandler.toLocalDateTime(endingDate).withNano(0) == IN_ELEVEN_DAYS.withNano(0)
            DateHandler.toLocalDateTime(applicationDeadline).withNano(0) == IN_NINE_DAYS.withNano(0)
        }

        cleanup:
        deleteAll()
    }

    def "login as member, and try to get activity suggestions list with an invalid institution id"() {
        given:
        demoMemberLogin()

        when:
        webClient.get()
                .uri("/activitySuggestions/9999")
                .headers { httpHeaders -> httpHeaders.putAll(headers) }
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def "login as a volunteer, and try to get activity suggestions list"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri("/activitySuggestions/" + institution.id)
                .headers { httpHeaders -> httpHeaders.putAll(headers) }
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def "login as an admin, and try to get activity suggestions list"() {
        given:
        demoAdminLogin()

        when:
        webClient.get()
                .uri("/activitySuggestions/" + institution.id)
                .headers { httpHeaders -> httpHeaders.putAll(headers) }
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }
}
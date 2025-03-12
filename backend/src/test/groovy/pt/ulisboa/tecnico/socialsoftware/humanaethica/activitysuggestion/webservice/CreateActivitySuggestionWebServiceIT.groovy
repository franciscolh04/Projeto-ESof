package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository.ActivitySuggestionRepository
import org.junit.jupiter.api.Test
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateActivitySuggestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution
    def activitySuggestionDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()

        activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TEN_DAYS)
    }

    def "login as Volunteer, and create an activity suggestion"() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri("/activitySuggestions/" + institution.id)
                .headers ( httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response data"
        response.name == ACTIVITY_NAME_1
        response.region == ACTIVITY_REGION_1
        response.participantsNumberLimit == 1
        response.description == ACTIVITY_DESCRIPTION_1
        response.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        response.startingDate == DateHandler.toISOString(IN_NINE_DAYS)
        response.endingDate == DateHandler.toISOString(IN_TEN_DAYS)

        and: "check database data"
        activitySuggestionRepository.count() == 1
        def activitySuggestion = activitySuggestionRepository.findAll().get(0)
        activitySuggestion.getName() == ACTIVITY_NAME_1
        activitySuggestion.getRegion() == ACTIVITY_REGION_1
        activitySuggestion.getParticipantsNumberLimit() == 1
        activitySuggestion.getDescription() == ACTIVITY_DESCRIPTION_1
        activitySuggestion.getApplicationDeadline().withNano(0) == IN_EIGHT_DAYS.withNano(0)
        activitySuggestion.getStartingDate().withNano(0) == IN_NINE_DAYS.withNano(0)
        activitySuggestion.getEndingDate().withNano(0) == IN_TEN_DAYS.withNano(0)

        cleanup:
        deleteAll()
    }

}
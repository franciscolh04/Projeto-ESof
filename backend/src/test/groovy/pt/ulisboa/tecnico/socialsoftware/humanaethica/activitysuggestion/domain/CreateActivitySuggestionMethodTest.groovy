package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateActivitySuggestionMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    def activitySuggestionDto

    def setup() {
        given: "dados da sugestão de atividade"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.name = "Campanha de Doação"
        activitySuggestionDto.region = "Lisboa"
        activitySuggestionDto.participantsNumberLimit = 10
        activitySuggestionDto.description = "Uma campanha para arrecadar alimentos."
        activitySuggestionDto.applicationDeadline = LocalDateTime.now().plusDays(10)
        activitySuggestionDto.startingDate = LocalDateTime.now().plusDays(15)
        activitySuggestionDto.endingDate = LocalDateTime.now().plusDays(20)
    }

    def "criar sugestão de atividade corretamente"() {
        given: "um voluntário e uma instituição"
        institution.getId() >> 1
        volunteer.getId() >> 1
        volunteer.getActivitySuggestions() >> new ArrayList<>()

        when: "uma sugestão de atividade é criada"
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then: "verifica os valores atribuídos"
        result.getInstitution() == institution
        result.getVolunteer() == volunteer
        result.getName() == "Campanha de Doação"
        result.getRegion() == "Lisboa"
        result.getParticipantsNumberLimit() == 10
        result.getDescription() == "Uma campanha para arrecadar alimentos."
        result.getApplicationDeadline() == activitySuggestionDto.applicationDeadline
        result.getStartingDate() == activitySuggestionDto.startingDate
        result.getEndingDate() == activitySuggestionDto.endingDate
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


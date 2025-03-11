package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class CreateActivitySuggestionMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherActivitySuggestion = Mock()
    def activitySuggestionDto

    def setup() {
        given: "activitySuggestion info"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.name = ACTIVITY_NAME_1
        activitySuggestionDto.region = ACTIVITY_REGION_1
        activitySuggestionDto.participantsNumberLimit = 10
        activitySuggestionDto.description = ACTIVITY_DESCRIPTION_1
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_EIGHT_DAYS)
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_NINE_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_TEN_DAYS)
    }

    def "creat an activity suggestion sucessfully"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        when: "an activity suggestion is created"
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then: "check result"
        result.getInstitution() == institution
        result.getVolunteer() == volunteer
        result.getName() == ACTIVITY_NAME_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getParticipantsNumberLimit() == 10
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getApplicationDeadline() == IN_EIGHT_DAYS
        result.getStartingDate() == IN_NINE_DAYS
        result.getEndingDate() == IN_TEN_DAYS
        and: "invocations"
        1 * institution.addActivitySuggestion(_)
        1 * volunteer.addActivitySuggestion(_)
    }

    @Unroll
    def "create activitySuggestion and violate description minimum length invariant: description=#description"() {

        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_1
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and: "an activity dto"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setName(ACTIVITY_NAME_1)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setParticipantsNumberLimit(10)
        activitySuggestionDto.setDescription(description)
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_EIGHT_DAYS))
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_NINE_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_TEN_DAYS))


        when: "an activity suggestion is created"
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_TOO_SHORT

        where:
        description << [null, "", "123456789"]
    }

    @Unroll
    def "create activitySuggestion and violate duplicated name suggestion by same volunteer invariant: name=#name"() {

        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_1
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and: "an activity suggestion dto is created"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.setName(name)
        activitySuggestionDto.setRegion(ACTIVITY_REGION_1)
        activitySuggestionDto.setParticipantsNumberLimit(10)
        activitySuggestionDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(IN_EIGHT_DAYS))
        activitySuggestionDto.setStartingDate(DateHandler.toISOString(IN_NINE_DAYS))
        activitySuggestionDto.setEndingDate(DateHandler.toISOString(IN_TEN_DAYS))

        when: "a new activity suggestion is created with the same name by the same volunteer"
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_ALREADY_MADE_BY_VOLUNTEER

        where:
        name << [ACTIVITY_NAME_1]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


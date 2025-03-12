package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateActivitySuggestionMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherActivitySuggestion = Mock()
    def activitySuggestionDto

    def setup() {
        given: "activitySuggestion info"
        activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,10,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TEN_DAYS)
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
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getApplicationDeadline() == IN_EIGHT_DAYS
        result.getStartingDate() == IN_NINE_DAYS
        result.getEndingDate() == IN_TEN_DAYS
        result.getParticipantsNumberLimit() == 10
        result.getState() == State.IN_REVIEW
        and: "invocations"
        1 * institution.addActivitySuggestion(_)
        1 * volunteer.addActivitySuggestion(_)
    }

    @Unroll
    def "create activitySuggestion and violate description minimum length invariant: description=#description"() {

        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_1
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and:
        activitySuggestionDto.setDescription(description)

        when: "an activity suggestion is created"
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_TOO_SHORT

        where:
        description << [null, "            ", "      1234     ", "123456789"]
    }

    @Unroll
    def "create activitySuggestion and violate duplicated name suggestion by same volunteer invariant: name=#name"() {

        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_1
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and:
        activitySuggestionDto.setName(name)

        when: "a new activity suggestion is created with the same name by the same volunteer"
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_ALREADY_MADE_BY_VOLUNTEER

        where:
        name << [ACTIVITY_NAME_1]
    }

    @Unroll
    def "create activitySuggestion and violate minimum application deadline invariant: deadline=#deadline"() {

        given:
        volunteer.getActivitySuggestions() >> []

        and:
        activitySuggestionDto.setApplicationDeadline(DateHandler.toISOString(deadline))

        when:
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_DEADLINE_TOO_SOON

        where:
        deadline << [TWO_DAYS_AGO, NOW, IN_SIX_DAYS]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}


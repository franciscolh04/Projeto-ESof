package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll


@DataJpaTest
class CreateActivitySuggestionServiceTest extends SpockTest {

    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'

    def institution
    def volunteer


    def setup() {
        institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
    }

    def "create activity suggestion"() {
        given: "an activity suggestion dto"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TEN_DAYS)

        when:
        def result = activitySuggestionService.createActivitySuggestion(
                volunteer.getId(),
                institution.getId(),
                activitySuggestionDto)

        then: "the returned data is correct"
        result.name == ACTIVITY_NAME_1
        result.region == ACTIVITY_REGION_1
        result.participantsNumberLimit == 1
        result.description == ACTIVITY_DESCRIPTION_1
        result.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        result.startingDate == DateHandler.toISOString(IN_NINE_DAYS)
        result.endingDate == DateHandler.toISOString(IN_TEN_DAYS)
        result.institutionId == institution.id
        result.volunteerId == volunteer.id
        result.getState() == ActivitySuggestion.State.IN_REVIEW.name()

        and: "the activity is saved in the database"
        activitySuggestionRepository.findAll().size() == 1

        and: "the stored data is correct"

        def storedActivitySuggestion = activitySuggestionRepository.findById(result.id).get()
        storedActivitySuggestion.name == ACTIVITY_NAME_1
        storedActivitySuggestion.region == ACTIVITY_REGION_1
        storedActivitySuggestion.participantsNumberLimit == 1
        storedActivitySuggestion.description == ACTIVITY_DESCRIPTION_1
        storedActivitySuggestion.applicationDeadline == IN_EIGHT_DAYS
        storedActivitySuggestion.startingDate == IN_NINE_DAYS
        storedActivitySuggestion.endingDate == IN_TEN_DAYS
        storedActivitySuggestion.institution.id == institution.id
        storedActivitySuggestion.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteer=#volunteerId, institution=#institutionId'() {
        given: "an activity suggestion dto"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_NINE_DAYS,IN_TEN_DAYS)
        when:
        def result = activitySuggestionService.createActivitySuggestion(
                getVolunteerId(volunteerId),
                getInstitutionId(institutionId),
                activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        and: "no activity is stored in the database"
        activitySuggestionRepository.findAll().size() == 0

        where:
        volunteerId   | institutionId || errorMessage
        null          | EXIST         || ErrorMessage.USER_NOT_FOUND
        NO_EXIST      | EXIST         || ErrorMessage.USER_NOT_FOUND
        EXIST         | null          || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST         | NO_EXIST      || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def getInstitutionId(institutionId) {
        if (institutionId == EXIST)
            return institution.getId()
        else if (institutionId == NO_EXIST)
            return 222
        else
            return null
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST)
            return volunteer.getId()
        else if (volunteerId == NO_EXIST)
            return 222
        else
            return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

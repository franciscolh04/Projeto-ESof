package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@DataJpaTest
class CreateActivitySuggestionServiceTest extends SpockTest {

    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    public static final String SHORTDESCRIPTION = "This is a short description"

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
        storedActivitySuggestion.startingDate == IN_NINE_DAYS
        storedActivitySuggestion.endingDate == IN_TEN_DAYS
        storedActivitySuggestion.applicationDeadline == IN_EIGHT_DAYS
        storedActivitySuggestion.institution.id == institution.id
        storedActivitySuggestion.volunteer.id == volunteer.id
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

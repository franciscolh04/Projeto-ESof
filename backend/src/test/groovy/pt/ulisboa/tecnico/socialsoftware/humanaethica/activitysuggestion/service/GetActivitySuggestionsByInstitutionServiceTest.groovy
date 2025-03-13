package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;

@DataJpaTest
class GetActivitySuggestionsByInstitutionServiceTest extends SpockTest {

    def institution;
    def volunteer;
    def activitySuggestionDto1;
    def activitySuggestionDto2;

    def setup() {
        institution = institutionService.getDemoInstitution();
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        given: "two activity suggestions associated with an institution"
        activitySuggestionDto1 = createActivitySuggestionDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 10, ACTIVITY_DESCRIPTION_1,
                IN_NINE_DAYS, IN_TEN_DAYS, IN_ELEVEN_DAYS);

        activitySuggestionDto2 = createActivitySuggestionDto(ACTIVITY_NAME_2, ACTIVITY_REGION_2, 15, ACTIVITY_DESCRIPTION_2,
                IN_EIGHT_DAYS, IN_NINE_DAYS, IN_TEN_DAYS);

        and: "two activities activities"
        activitySuggestionService.createActivitySuggestion(volunteer.getId(), institution.getId(), activitySuggestionDto1);
        activitySuggestionService.createActivitySuggestion(volunteer.getId(), institution.getId(), activitySuggestionDto2);

    }

    def "get activity suggestions by institution successfully"() {
        when:
        List<ActivitySuggestionDto> result = activitySuggestionService.getActivitySuggestionsByInstitution(institution.getId());

        then: "the list has the two activity suggestions and is ordered by starting date"
        result.size() == 2
        result[0].getName() == ACTIVITY_NAME_2
        result[1].getName() == ACTIVITY_NAME_1

        and: "the suggestions are associated with the right institution"
        result.each {
            assert it.getInstitutionId() == institution.getId()
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
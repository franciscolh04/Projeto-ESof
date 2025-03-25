package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

@DataJpaTest
class GetInstitutionProfileServiceTest extends SpockTest {
    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()
    }

    def "get Institution Profile Sucessfully"(){
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        def institutionProfile = createInstitutionProfile(institution, institutionProfileDto)
        
        when:
        def result = institutionProfileService.getInstitutionProfile(institution.id)

        then:
        result.institutionId == institution.id
        result.shortDescription == SHORTDESCRIPTION
    }


    def "invalid arguments: institution=#institutionid"() {
        when:
        institutionProfileService.getInstitutionProfile(institutionid)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        institutionid   || errorMessage
        null            || ErrorMessage.INSTITUTION_NOT_FOUND
        222             || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def "invalid arguments: institution profile"() {
        when:
        institutionProfileService.getInstitutionProfile(institution.id)
        
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_PROFILE_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
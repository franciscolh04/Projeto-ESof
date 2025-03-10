package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto
import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileServiceTest extends SpockTest {
    
    @Autowired
    InstitutionProfileService institutionProfileService

    @Autowired
    InstitutionProfileRepository institutionProfileRepository

    def institution
    def member

    def setup() {
        institution = institutionService.getDemoInstitution()
        member = authUserService.loginDemoMemberAuth().getUser()
    }

    def 'create institution profile' () {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = 'short description'

        when:
        def result = institutionProfileService.createInstitutionProfile(member.id, institution.id, institutionProfileDto)

        then:
        result.shortDescription == 'short description'
        and:
        institutionProfileRepository.findAll().size() == 1
        def storedInstitutionProfile = institutionProfileRepository.findAll().get(0)
        storedInstitutionProfile.shortDescription == 'short description'
        storedInstitutionProfile.institution.id == institution.id
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        @Bean
        InstitutionProfileService institutionProfileService() {
            return new InstitutionProfileService()
        }
    }
}
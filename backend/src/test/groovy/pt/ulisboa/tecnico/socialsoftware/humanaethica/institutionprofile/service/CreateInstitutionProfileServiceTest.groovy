package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileServiceTest extends SpockTest {

    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'


    def institution
    def member

    def setup() {
        institution = institutionService.getDemoInstitution()
        member = authUserService.loginDemoMemberAuth().getUser()
    }

    def 'create institution profile' () {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        when:
        def result = institutionProfileService.createInstitutionProfile(institution.id, institutionProfileDto)

        then:
        result.shortDescription == SHORTDESCRIPTION
        and:
        institutionProfileRepository.findAll().size() == 1
        def storedInstitutionProfile = institutionProfileRepository.findAll().get(0)
        storedInstitutionProfile.shortDescription == SHORTDESCRIPTION
        storedInstitutionProfile.institution.id == institution.id
    }

    @Unroll
    def 'invalid arguments: institution=#institutionid | instProfDto=#value'() {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        and:
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        when:
        institutionProfileService.createInstitutionProfile(getinstitutionid(institutionid), getinstitutionProfileDto(value,institutionProfileDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        institutionProfileRepository.findAll().size() == 0

        where:
        institutionid | value              || errorMessage
        null          | EXIST              || ErrorMessage.INSTITUTION_NOT_FOUND
        NO_EXIST      | EXIST              || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST         | null               || ErrorMessage.INVALID_INSTITUTION_PROFILE

    }

    def getinstitutionid(institutionid) {
        if (institutionid == EXIST)
            return institution.id
        else if (institutionid == NO_EXIST)
            return 222
        else
            return null
    }

    def getinstitutionProfileDto(value, institutionProfileDto) {
        if (value == EXIST) {
            return institutionProfileDto
        }
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
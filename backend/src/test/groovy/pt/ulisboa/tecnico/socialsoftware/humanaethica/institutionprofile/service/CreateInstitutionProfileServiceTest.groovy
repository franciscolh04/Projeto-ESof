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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileServiceTest extends SpockTest {

    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'

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

    @Unroll
    def 'invalid arguments: userId=#userId | institutionId=#institutionId'() {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        and:
        institutionProfileDto.shortDescription = shortDescription

        when:
        institutionProfileService.createInstitutionProfile(getuserId(userId), getinstitutionId(institutionId), getinstitutionProfileDto(value,institutionProfileDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        // FIX: institutionProfileRepository.findAll().size() == 0

        where:
        userId      | institutionId | value              | shortDescription       || errorMessage
        null        | EXIST         | EXIST              | "short description"    || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST         | EXIST              |  "short description"   || ErrorMessage.USER_NOT_FOUND
        EXIST       | null          | EXIST              |  "short description"   || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | NO_EXIST      | EXIST              |  "short description"   || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | EXIST         | null               |    "short description" || ErrorMessage.INVALID_INSTITUTION_PROFILE
        EXIST       | EXIST         | EXIST              |    null                || ErrorMessage.INVALID_SHORT_DESCRIPTION


    }

    @Unroll
    def 'invalid arguments: description=#description '() {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = description

        when:
        institutionProfileService.createInstitutionProfile(member.id, institution.id, institutionProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        //institutionProfileRepository.count() == 0

        where:
        description            || errorMessage
        "   1111111 1   "      || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT
        ""                     || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT
        "    "                 || ErrorMessage.INSTITUTION_PROFILE_DESCRIPTION_TOO_SHORT

    }

    def getuserId(userId) {
        if (userId == EXIST)
            return member.id
        else if (userId == NO_EXIST)
            return 222
        else
            return null
    }

    def getinstitutionId(institutionId) {
        if (institutionId == EXIST)
            return institution.id
        else if (institutionId == NO_EXIST)
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
    static class LocalBeanConfiguration extends BeanConfiguration {
        @Bean
        InstitutionProfileService institutionProfileService() {
            return new InstitutionProfileService()
        }
    }
}
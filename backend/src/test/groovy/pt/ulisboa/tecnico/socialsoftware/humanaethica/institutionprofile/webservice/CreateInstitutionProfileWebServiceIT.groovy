package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.InstitutionProfileRepository
import org.junit.jupiter.api.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateInstitutionProfileWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def institution
    def institutionProfileDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        // getDemoInstitution() adds it to the repository
        institution = institutionService.getDemoInstitution()

        institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = SHORTDESCRIPTION
    }
    
    def "create institution profile"() {
        given: 
        demoMemberLogin()

        when: 
        def response = webClient.post()
            .uri("/institutionProfile/" + institution.id + "/profile")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(institutionProfileDto)
            .retrieve()
            .bodyToMono(InstitutionProfileDto.class)
            .block()

        then:
        response.shortDescription == SHORTDESCRIPTION

        and:
        institutionProfileRepository.findAll().size() == 1
        def storedInstitutionProfile = institutionProfileRepository.findAll().get(0)
        storedInstitutionProfile.shortDescription == SHORTDESCRIPTION
        storedInstitutionProfile.institution.id == institution.id

        cleanup:
        deleteAll()
    }
}
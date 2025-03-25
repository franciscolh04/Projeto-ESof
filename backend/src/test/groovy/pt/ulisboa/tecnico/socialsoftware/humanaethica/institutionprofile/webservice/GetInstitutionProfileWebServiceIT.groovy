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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile
import org.junit.jupiter.api.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetInstitutionProfileWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def institution
    def institutionProfileDto
    def institutionProfile

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()
        

        institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = SHORTDESCRIPTION

        institutionProfile = new InstitutionProfile(institution, institutionProfileDto)
        institutionProfileRepository.save(institutionProfile)
    }

    def "non authenticated user get institution Profile"(){
       when:
        def response = webClient.get()
                .uri('/institutionProfile/' + institution.id + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(InstitutionProfileDto.class)
                .collectList()
                .block()

        then:
        response.size() == 1
        response.get(0).institutionId == institution.id
        response.get(0).shortDescription == SHORTDESCRIPTION
    }

    
    def 'institution does not exist'() {
        when:
        def response = webClient.get()
                .uri('/institutionProfile/' + 222 + '/profile')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(InstitutionProfileDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
    }

}
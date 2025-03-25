package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto;

import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;

@RestController
@RequestMapping("/institutionProfile")
public class InstitutionProfileController {
    @Autowired
    InstitutionProfileService institutionProfileService;

    @PostMapping("/{institutionId}/profile")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public InstitutionProfileDto createInstitutionProfile(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody InstitutionProfileDto institutionProfileDto) {
        // Principal is not used because hasPermission(#institutionId, 'INSTITUTION.MEMBER') deals with the restriction
        return institutionProfileService.createInstitutionProfile(institutionId, institutionProfileDto);
    }

    @GetMapping("/{institutionId}/profile")
    public InstitutionProfileDto getInstitutionProfile(@PathVariable Integer institutionId) {
        return institutionProfileService.getInstitutionProfile(institutionId);
    }
}



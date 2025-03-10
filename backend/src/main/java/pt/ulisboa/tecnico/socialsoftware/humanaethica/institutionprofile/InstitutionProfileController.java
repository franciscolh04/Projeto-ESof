package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

public class InstitutionProfileController {
    @Autowired
    InstitutionProfileService institutionProfileService;

    @PostMapping("/institutions/{institutionId}/profile")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public InstitutionProfileDto createInstitutionProfile(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody InstitutionProfileDto institutionProfileDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return institutionProfileService.createInstitutionProfile(userId, institutionId, institutionProfileDto);
    }
}

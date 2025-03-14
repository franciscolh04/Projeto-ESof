package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerProfileController {
    @Autowired
    private VolunteerProfileService volunteerProfileService;

    private static final Logger logger = LoggerFactory.getLogger(VolunteerProfileController.class);

    @GetMapping("/{volunteerId}/profile")
    public VolunteerProfileDto getVolunteerProfile(@PathVariable Integer volunteerId) {
        return volunteerProfileService.getVolunteerProfile(volunteerId);
    }

    @PostMapping("/{volunteerId}/profile")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public VolunteerProfileDto createVolunteerProfile(Principal principal, @PathVariable Integer volunteerId, @Valid @RequestBody VolunteerProfileDto volunteerProfileDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return volunteerProfileService.createVolunteerProfile(userId, volunteerProfileDto);
    }
}
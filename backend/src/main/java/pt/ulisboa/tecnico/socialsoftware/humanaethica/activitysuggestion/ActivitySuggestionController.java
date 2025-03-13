package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import java.util.List;

import java.security.Principal;

@RestController
@RequestMapping("/activitySuggestions")
public class ActivitySuggestionController {

    @Autowired
    private ActivitySuggestionService activitySuggestionService;

    @PostMapping("/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivitySuggestionDto createActivitySuggestion(Principal principal,
                                                          @PathVariable Integer institutionId,
                                                          @Valid @RequestBody ActivitySuggestionDto activitySuggestionDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activitySuggestionService.createActivitySuggestion(userId, institutionId, activitySuggestionDto);
    }

    @GetMapping("/{institutionId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#institutionId, 'INSTITUTION.MEMBER')")
    public List<ActivitySuggestionDto> getActivitySuggestionsByInstitution(@PathVariable Integer institutionId) {
        return activitySuggestionService.getActivitySuggestionsByInstitution(institutionId);
    }
}
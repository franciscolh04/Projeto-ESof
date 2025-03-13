package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.repository.ActivitySuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class ActivitySuggestionService {

    @Autowired
    private ActivitySuggestionRepository activitySuggestionRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActivitySuggestionDto createActivitySuggestion(Integer userId, Integer institutionId,
                                                          ActivitySuggestionDto activitySuggestionDto) {

        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId)
                .orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        ActivitySuggestion activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto);
        activitySuggestionRepository.save(activitySuggestion);

        return new ActivitySuggestionDto(activitySuggestion);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ActivitySuggestionDto> getActivitySuggestionsByInstitution(Integer institutionId) {

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        return institution.getActivitySuggestions().stream()
                .map(ActivitySuggestionDto::new)
                .sorted(Comparator.comparing(ActivitySuggestionDto::getCreationDate))
                .collect(Collectors.toList());
    }

}
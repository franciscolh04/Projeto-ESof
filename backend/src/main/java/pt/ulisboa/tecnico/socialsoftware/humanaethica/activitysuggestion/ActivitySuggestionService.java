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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public ActivitySuggestionDto createActivitySuggestion(Integer volunteerId, Integer institutionId, ActivitySuggestionDto dto) {

        if (dto == null)
            throw new HEException(ACTIVITY_SUGGESTION_INVALID);
        if (dto.getDescription().trim().length() < 10)
            throw new HEException(ACTIVITY_SUGGESTION_DESCRIPTION_TOO_SHORT);
        if (dto.getApplicationDeadline().isBefore(dto.getCreationDate().plusDays(7)))
            throw new HEException(ACTIVITY_SUGGESTION_DEADLINE_TOO_SOON);

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        Volunteer volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new HEException(VOLUNTEER_NOT_FOUND));

        // Verifica se o voluntário já sugeriu uma atividade com o mesmo nome
        boolean alreadyExists = activitySuggestionRepository.existsByNameAndVolunteer(dto.getName(), volunteer);
        if (alreadyExists) {
            throw new HEException(ACTIVITY_SUGGESTION_ALREADY_MADE_BY_VOLUNTEER);
        }

        ActivitySuggestion activitySuggestion = new ActivitySuggestion(institution, volunteer, dto);
        activitySuggestionRepository.save(activitySuggestion);

        return new ActivitySuggestionDto(activitySuggestion);
    }

}
package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.dto.VolunteerProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.repository.VolunteerProfileRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class VolunteerProfileService {
    @Autowired
    VolunteerProfileRepository volunteerProfileRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public VolunteerProfileDto createVolunteerProfile(Integer userId, VolunteerProfileDto volunteerProfileDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto);

        volunteerProfileRepository.save(volunteerProfile);

        return new VolunteerProfileDto(volunteerProfile);
    }
}
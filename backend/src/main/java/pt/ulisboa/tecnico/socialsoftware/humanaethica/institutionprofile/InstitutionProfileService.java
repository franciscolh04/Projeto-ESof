package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.dto.InstitutionProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class InstitutionProfileService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionProfileRepository institutionProfileRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionProfileDto createInstitutionProfile(Integer userId, Integer institutionId, InstitutionProfileDto institutionProfileDto) {
        
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Member member = (Member) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        
        if (institutionProfileDto == null) throw new HEException(INVALID_INSTITUTION_PROFILE);
        if (institutionProfileDto.getShortDescription() == null) throw new HEException(INVALID_SHORT_DESCRIPTION);
        
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId)
        .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        // FIX:  if (member.getInstitution().getId() != institutionId) throw new HEException(MEMBER_NOT_IN_INSTITUTION);

        InstitutionProfile institutionProfile = new InstitutionProfile(institution, institutionProfileDto);
        institutionProfileRepository.save(institutionProfile);

        return new InstitutionProfileDto(institutionProfile);
    }

}

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

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class InstitutionProfileService {

    @Autowired
    private InstitutionProfileRepository institutionProfileRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionProfileDto createInstitutionProfile(Integer institutionId, InstitutionProfileDto institutionProfileDto) {
                
        if (institutionProfileDto == null) throw new HEException(INVALID_INSTITUTION_PROFILE);
        
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId)
        .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        InstitutionProfile institutionProfile = new InstitutionProfile(institution, institutionProfileDto);
        institutionProfileRepository.save(institutionProfile);

        return new InstitutionProfileDto(institutionProfile);
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InstitutionProfileDto getInstitutionProfile(Integer institutionId){

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        Institution institution = institutionRepository.findById(institutionId)
        .orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND));

        InstitutionProfile institutionProfile = institutionProfileRepository.getInstitutionProfileByInstitutionID(institutionId)
        .orElseThrow(() -> new HEException(INSTITUTION_PROFILE_NOT_FOUND));

        return new InstitutionProfileDto(institutionProfile);
    }

}

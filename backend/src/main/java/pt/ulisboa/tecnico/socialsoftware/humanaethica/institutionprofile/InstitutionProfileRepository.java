package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
@Transactional
public interface InstitutionProfileRepository extends JpaRepository<InstitutionProfile, Integer> {
    
    @Query(value = "SELECT * FROM institution_profile WHERE institution_id = :institutionId",nativeQuery = true)
    Optional<InstitutionProfile> getInstitutionProfileByInstitutionID(Integer institutionId);

}

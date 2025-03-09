package pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institutionprofile.domain.InstitutionProfile;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface InstitutionProfileRepository extends JpaRepository<InstitutionProfile, Integer> {
    
    

}

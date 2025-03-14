package pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.volunteerprofile.domain.VolunteerProfile;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Integer> {

}
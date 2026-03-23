//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.entity.Survey;
import at.technikum.springrestbackend.entity.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;



//#######################################################################
//#######################################################################
//#######################################################################
// interface
@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {

    List<Survey> findByStatus(SurveyStatus status);

    List<Survey> findByCreatorId(UUID creatorId);

    List<Survey> findByStatusOrderByCreatedAtDesc(SurveyStatus status);
}

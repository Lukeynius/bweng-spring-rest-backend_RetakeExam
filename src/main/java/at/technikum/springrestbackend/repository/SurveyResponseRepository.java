//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.repository;


import at.technikum.springrestbackend.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// interface
@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {

    List<SurveyResponse> findBySurveyId(UUID surveyId);

    List<SurveyResponse> findByUserId(UUID userId);

    Optional<SurveyResponse> findByUserIdAndSurveyId(UUID userId, UUID surveyId);

    boolean existsByUserIdAndSurveyId(UUID userId, UUID surveyId);
}

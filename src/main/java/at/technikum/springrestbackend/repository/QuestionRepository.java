//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.repository;


import at.technikum.springrestbackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;



//#######################################################################
//#######################################################################
//#######################################################################
// interface
@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findBySurveyId(UUID surveyId);
}

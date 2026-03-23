//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.repository;


import at.technikum.springrestbackend.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// interface
@Repository
public interface OptionRepository  extends JpaRepository<Option, UUID> {

}

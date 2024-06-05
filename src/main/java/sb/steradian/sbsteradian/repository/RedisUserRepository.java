package sb.steradian.sbsteradian.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sb.steradian.sbsteradian.entity.RedisUser;

import java.util.UUID;

@Repository
public interface RedisUserRepository extends CrudRepository<RedisUser, UUID> {

    RedisUser findByUsername(String username);


}

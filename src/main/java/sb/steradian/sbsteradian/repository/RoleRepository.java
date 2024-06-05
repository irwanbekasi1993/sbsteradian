package sb.steradian.sbsteradian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sb.steradian.sbsteradian.entity.Role;

import javax.transaction.Transactional;
import java.util.*;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    Optional<Role> findById(UUID id);

    @Query(value="select r.* from role r " +
            "join user_role ur on ur.role_id=r.id " +
            "join users u on u.id=ur.user_id" +
            " where u.username=:name",nativeQuery=true)
    List<Role> getAllRolesByUserName(@Param("name") String name);

    @Query(value="select r.* from role r " +
            "join user_role ur on ur.role_id=r.id " +
            "join users u on u.id=ur.user_id" +
            " where u.username=:name",nativeQuery=true)
    Role getByUserName(@Param("name") String name);


    @Transactional
    @Modifying
    @Query(value = "update role set name=:updatedName,description=:description where name=:originName",nativeQuery = true)
    int updateByName(@Param("updatedName")String updatedName
            ,@Param("description")String description
            ,@Param("originName")String originName);

}

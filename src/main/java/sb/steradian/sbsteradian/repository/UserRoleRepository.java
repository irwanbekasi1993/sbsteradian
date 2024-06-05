package sb.steradian.sbsteradian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sb.steradian.sbsteradian.entity.UserRole;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    Optional<UserRole> findByUserId(UUID userId);

    @Query(value = "select ur.* from user_role ur join users u on u.id=ur.user_id where u.username=:username",nativeQuery = true)
    UserRole getByUsername(@Param("username")String username);

    @Query(value = "select * from user_role where user_id=:userId and role_id=:roleId",nativeQuery = true)
    UserRole getByUserIdAndRoleId(@Param("userId")UUID userId, @Param("roleId")UUID roleId);

    @Query(value = "select * from user_role where user_id=:userId or role_id=:roleId",nativeQuery = true)
    UserRole getByUserIdOrRoleId(@Param("userId")UUID userId, @Param("roleId")UUID roleId);

    @Query(value = "select ur.* from user_role ur join users u" +
            " on u.id=ur.user_id join role r on r.id=ur.role_id " +
            "where u.username=:username and r.name=:name",nativeQuery = true)
    UserRole getByUsernameAndRoleName(@Param("username")String username,@Param("name")String name);

    @Transactional
    @Modifying
    @Query(value = "update user_role set user_id=:updatedUserId, role_id=:updatedRoleId " +
            "where id=:id",nativeQuery = true)
    int updateUserRole(@Param("updatedUserId")UUID updatedUserId,
                       @Param("updatedRoleId")UUID updatedRoleId,
                       @Param("id")UUID id);
}

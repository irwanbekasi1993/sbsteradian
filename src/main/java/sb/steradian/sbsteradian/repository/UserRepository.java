package sb.steradian.sbsteradian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sb.steradian.sbsteradian.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query(value = "select * from users where id=:id",nativeQuery = true)
    Optional<User> getUserById(@Param("id") UUID id);

    @Query(value = "select * from users ",nativeQuery = true)
    List<User> findAllUsers();

    @Modifying
    @Transactional
    @Query(value = "update users set password=:newPassword where username=:username ",nativeQuery = true)
    void resetPassword(@Param("newPassword") String newPassword, @Param("username") String username);



    @Modifying
    @Transactional
    @Query(value = "update users set username=:username,name=:name,nickname=:nickName,email=:email,phone_number=:phoneNumber where id=:id",nativeQuery = true)
    int updateUser(@Param("username")String username, @Param("name")String name,@Param("nickName")String nickName,@Param("email")String email,@Param("phoneNumber")String phoneNumber,@Param("id")UUID id);


    @Modifying
    @Transactional
    @Query(value = "update users set status='deleted' where id=:id",nativeQuery = true)
    int updateDeletedUser(@Param("id")UUID id);
}

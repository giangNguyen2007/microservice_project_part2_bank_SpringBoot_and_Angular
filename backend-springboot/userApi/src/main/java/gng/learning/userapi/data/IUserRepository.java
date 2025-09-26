package gng.learning.userapi.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID> {

    public Optional<UserModel> findByEmail(String email);
    public Optional<UserModel> findByName(String name);
}

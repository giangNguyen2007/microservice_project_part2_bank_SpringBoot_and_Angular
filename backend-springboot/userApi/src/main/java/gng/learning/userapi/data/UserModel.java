package gng.learning.userapi.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Table(name = "users") // set table
//@Data // Lombok => create automatically getter and setter
//@NoArgsConstructor // Lombok => generated a no arg constructor => used by JPA to create User object, then set field
//@AllArgsConstructor
// Lombok => generated a constructor with one arg for each field => used by JPA to create User object
public class UserModel {

    // no argument constructor needed by JPA
    public UserModel() {
    }
    public UserModel(UUID userId, String name, String email, String password, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private Role role = Role.USER; // default role


    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


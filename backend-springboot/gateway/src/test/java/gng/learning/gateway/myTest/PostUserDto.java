package gng.learning.gateway.myTest;

public class PostUserDto {

    public String name;

    public String email;

    public String password;

    public PostUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

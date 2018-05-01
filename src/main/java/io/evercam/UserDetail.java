package io.evercam;

public class UserDetail {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String countrycode = "";

    public String getFirstname() throws EvercamException {
        if (firstname == null) {
            throw new EvercamException("firstname is missing");
        }
        return firstname;
    }

    public String getLastname() throws EvercamException {
        if (lastname == null) {
            throw new EvercamException("lastname is missing");
        }
        return lastname;
    }

    public String getUsername() throws EvercamException {
        if (username == null) {
            throw new EvercamException("username is missing");
        }
        return username;
    }

    public String getPassword() throws EvercamException {
        if (password == null) {
            throw new EvercamException("password is missing");
        }
        return password;
    }

    public String getEmail() throws EvercamException {
        if (email == null) {
            throw new EvercamException("email is missing");
        }
        return email;
    }

    public String getCountryCode() {
        return countrycode;
    }

    public boolean hasCountryCode() {
        return !getCountryCode().isEmpty();
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}

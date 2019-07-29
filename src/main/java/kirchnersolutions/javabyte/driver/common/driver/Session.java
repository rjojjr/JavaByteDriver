package kirchnersolutions.javabyte.driver.common.driver;

import java.math.BigInteger;

public class Session {

    private String username = "";
    private BigInteger password = null;
    private User user = null;

    public Session(String username, BigInteger password){
        this.username = username;
        this.password = password;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }
}

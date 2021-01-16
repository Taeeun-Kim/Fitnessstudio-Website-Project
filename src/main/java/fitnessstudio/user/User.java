package fitnessstudio.user;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private UserAccount userAccount;

    @OneToOne(cascade = {CascadeType.ALL})
    private UserAddress address;

    public User(UserAccount userAccount, UserAddress address) {
        this.userAccount = userAccount;
        this.address = address;

    }

    protected User() {}

    public long getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAddress getAddress() {
        return address;
    }

    public boolean isEnabled() {
        return userAccount.isEnabled();
    }
    
}

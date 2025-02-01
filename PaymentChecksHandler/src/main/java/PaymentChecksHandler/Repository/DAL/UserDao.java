package main.java.PaymentChecksHandler.Repository.DAL;

public interface UserDao {

    User getUserDetails(String email);

    Long insertUserDetails(User user);

}

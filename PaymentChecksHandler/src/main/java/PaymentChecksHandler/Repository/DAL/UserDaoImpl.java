package main.java.PaymentChecksHandler.Repository.DAL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cpt.payments.dao.UserDao;
import com.cpt.payments.dto.User;
import com.cpt.payments.util.LogMessage;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public User getUserDetails(String userEmail) {

        LogMessage.log(logger, " :: Retrieving user information for email :: " + userEmail);

        User user = null;
        try {
            user = jdbcTemplate.queryForObject(buildUserFetchQuery(),
                    new BeanPropertySqlParameterSource(User.builder().email(userEmail).build()),
                    new BeanPropertyRowMapper<>(User.class));
            LogMessage.log(logger, " :: Retrieved user details from DB: " + user);
        } catch (Exception ex) {
            LogMessage.log(logger, "Error fetching user details for email " + userEmail + ": " + ex);
            LogMessage.logException(logger, ex);
        }
        return user;
    }

    private String buildUserFetchQuery() {
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE email = :email");
        LogMessage.log(logger, "Constructed SQL query: " + query);
        return query.toString();
    }

    @Override
    public Long insertUserDetails(User user) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(buildUserInsertQuery(), new BeanPropertySqlParameterSource(user), keyHolder);
            Long generatedId = keyHolder.getKey().longValue();
            user.setId(generatedId);
            return generatedId;
        } catch (Exception ex) {
            LogMessage.log(logger, "Error saving user information to DB: " + user);
            LogMessage.logException(logger, ex);
        }
        return null;
    }

    private String buildUserInsertQuery() {
        StringBuilder query = new StringBuilder("INSERT INTO users ");
        query.append("(email, phone_number, first_name, last_name) ");
        query.append("VALUES(:email, :phoneNumber, :firstName, :lastName)");
        LogMessage.log(logger, "Insert query: " + query);
        return query.toString();
    }
}

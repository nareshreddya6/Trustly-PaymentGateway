package main.java.PaymentChecksHandler.ChecksHandler.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import main.java.PaymentChecksHandler.ChecksHandler.constants.ErrorCodeEnum;
import main.java.PaymentChecksHandler.ChecksHandler.Repository.DAL.UserDao;
import main.java.PaymentChecksHandler.ChecksHandler.Repository.DAL.dto.User;
import main.java.PaymentChecksHandler.ChecksHandler.exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.service.UserService;
import main.java.PaymentChecksHandler.ChecksHandler.util.LogMessageUtil;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public Long registerUser(PaymentRequest paymentRequest) {
        LogMessageUtil.logMessage(logger, "Checking user details for: " + paymentRequest);

        User existingUser = userDao.getUserDetails(paymentRequest.getUser().getEmail());

        if (existingUser == null) {
            LogMessageUtil.logMessage(logger, "User not found, proceeding with new user creation.");

            User newUser = new User(
                    paymentRequest.getUser().getEmail(),
                    paymentRequest.getUser().getFirstName(),
                    paymentRequest.getUser().getLastName(),
                    paymentRequest.getUser().getPhoneNumber());

            Long userId = userDao.insertUserDetails(newUser);

            if (userId == null) {
                throw new ValidationException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCodeEnum.FAILED_TO_CREATE_USER.getErrorCode(),
                        ErrorCodeEnum.FAILED_TO_CREATE_USER.getErrorMessage());
            }
        }

        return existingUser != null ? existingUser.getId() : userId;
    }
}

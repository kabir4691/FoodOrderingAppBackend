package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerService {

  @Autowired
  CustomerDao customerDao;

  @Autowired
  CustomerAuthDao customerAuthDao;

  @Autowired
  PasswordCryptographyProvider passwordCryptographyProvider;

  private final String EMAIL_REGEX = '^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$';

  private final String CONTACT_NUMBER_REGEX = "(0/91)?[7-9][0-9]{9}";

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {

    CustomerEntity existingCustomer = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());
    if (existingCustomer != null) {
      throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
    }

    if (customerEntity.getFirstName() == null
    || customerEntity.getFirstName() == ""
    || customerEntity.getPassword() == null
    || customerEntity.getPassword() == ""
    || customerEntity.getEmail() == null
    || customerEntity.getEmail() == ""
    || customerEntity.getContactNumber() == null
    || customerEntity.getContactNumber() == "") {
      throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
    }

    if (!(customerEntity.getEmail().matches(EMAIL_REGEX))) {
      throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
    }

    Pattern pattern = Pattern.compile(CONTACT_NUMBER_REGEX);
    Matcher matcher = pattern.matcher(customerEntity.getContactNumber());
    if (!(matcher.find() && matcher.group().equals(customerEntity.getContactNumber()))) {
      throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
    }

    if (!(isPasswordStrong(customerEntity.getPassword()))) {
      throw new SignUpRestrictedException("SGR-004", "Weak password!");
    }

    String[] encrypted = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
    customerEntity.setSalt(encrypted[0]);
    customerEntity.setPassword(encrypted[1]);

    return customerDao.createCustomer(customerEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity login(String contactNumber, String password) throws AuthenticationFailedException {

    CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
    if (customerEntity == null) {
      throw new AuthenticationFailedException("ATH-001","This contact number has not been registered!");
    }

    String encrypted = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());

    if (!(encrypted.equals(customerEntity.getPassword()))) {
      throw new AuthenticationFailedException("ATH-002","Invalid Credentials");
    }

    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
    CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
    customerAuthEntity.setCustomer(customerEntity);

    final ZonedDateTime nowTime = ZonedDateTime.now();
    final ZonedDateTime expiresAtTime = now.plusHours(8);

    customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), nowTime, expiresAtTime));
    customerAuthEntity.setLoginAt(nowTime);
    customerAuthEntity.setExpiresAt(expiresAtTime);
    customerAuthEntity.setUuid(UUID.randomUUID().toString());

    return customerAuthDao.createCustomerAuth(customerAuthEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {

    CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

    if (customerAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    }

    if (customerAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
    }

    final ZonedDateTime nowTime = ZonedDateTime.now();

    if (customerAuthEntity.getExpiresAt().compareTo(nowTime) < 0) {
      throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
    }

    customerAuthEntity.setLogoutAt(nowTime);

    return customerAuthDao.updateCustomerAuth(customerAuthEntity);
  }

  private boolean isPasswordStrong(String password) {
      
    if (password.length() < 8) {
      return false;
    }
    
    if (!(password.matches("(?=.*[0-9]).*"))) {
      return false;
    }

    if (!(password.matches("(?=.*[a-z]).*"))) {
      return false
    }
    
    if (!(password.matches("(?=.*[A-Z]).*"))) {
      return false;
    }

    if (!(password.matches("(?=.*[#@$%&*!^]).*"))) {
      return false
    }

    return true;
  }
}
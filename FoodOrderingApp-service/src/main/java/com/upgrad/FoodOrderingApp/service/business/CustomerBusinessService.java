package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
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
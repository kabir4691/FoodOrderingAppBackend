package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/** Controller class for handling all customer related endpoints */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerBusinessService CustomerBusinessService;

    @Autowired
    UtilityProvider utilityProvider; 

    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());

        CustomerEntity createdCustomerEntity = CustomerBusinessService.signup(customerEntity);

        SignupCustomerResponse response = new SignupCustomerResponse()
        .id(createdCustomerEntity.getUuid())
        .status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(
            response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> customerLogin (@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        
        String authArray = authorization.split("Basic ");
        byte[] decodedArray = Base64.getDecoder().decode(authArray[1]);
        String decodedString = new String(decodedArray);
        String[] credentialsArray = decodedString.split(":");

        CustomerAuthEntity customerAuthEntity = customerBusinessService.login(decodedArray[0],decodedArray[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", customerAuthEntity.getAccessToken());

        CustomerEntity customerEntity = customerAuthEntity.getCustomer();
        LoginResponse response = new LoginResponse()
            .id(customerEntity.getUuid())
            .contactNumber(customerEntity.getContactNumber())
            .emailAddress(customerEntity.getEmail())
            .firstName(customerEntity.getFirstName())
            .lastName(customerEntity.getLastName())
            .message("LOGGED IN SUCCESSFULLY");

        return new ResponseEntity<LoginResponse>(response, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthEntity customerAuthEntity = customerBusinessService.customerLogout(accessToken);

        LogoutResponse logoutResponse = new LogoutResponse()
            .id(customerAuthEntity.getCustomer().getUuid())
            .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateDetails(@RequestHeader("authorization") final String authorization, UpdateCustomerRequest updateCustomerRequest) throws AuthenticationFailedException, UpdateCustomerException {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity updatedCustomerEntity = customerBusinessService.updateCustomerDetails(customerEntity, accessToken);

        UpdateCustomerResponse response = new UpdateCustomerResponse()
                .firstName(updatedCustomerEntity.getFirstName())
                .lastName(updatedCustomerEntity.getLastName())
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(
            response, HttpStatus.OK);
    }
}

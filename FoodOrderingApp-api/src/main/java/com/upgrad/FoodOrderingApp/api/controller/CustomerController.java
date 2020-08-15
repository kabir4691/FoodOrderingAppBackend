package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
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




// Customer Controller Handles all  the customer related endpoints

@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService; // Handles all the Service Related Customer.

    @Autowired
    UtilityProvider utilityProvider; // It Provides Data Check methods for various cases


    /* The method handles Customer SignUp Related request.It takes the details as per in the SignupCustomerRequest
    & produces response in SignupCustomerResponse and returns UUID of newly Created Customer and Success message else Return error code and error Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUpCustomer(@RequestBody(required = false)  final SignupCustomerRequest signupCustomerRequest)throws SignUpRestrictedException {
        //Creating new Customer entity from the request data
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());

        //Checking if the Request Data is valid and has all the fields required.
        utilityProvider.isValidSignupRequest(customerEntity);

        //Passing the Customer entity to the customerService saveCustomer method to persist in the database.
        CustomerEntity signedUpCustomer =  customerService.saveCustomer(customerEntity);

        //Creating Response for the Request
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(signedUpCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse,HttpStatus.CREATED);
    }


    /*This Method handles the Login request and takes authorization parameter in Base64 coded and produces a LoginResponse containing info customer
    and response header containing bearer accessToken. If error returns the error code with corresponding Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "/login",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> customerLogin (@RequestHeader("authorization") final String authorization)throws AuthenticationFailedException {

        //Checking if the authorization is in valid format
        utilityProvider.isValidAuthorizationFormat(authorization);

        //Separating the username and password after decoding using Base64 decoder
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");

        // Calls CustomerService method authenticate to authenticate the login request. if authenticated it returns CustomerAuthEntity conating the details as required.
        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodedArray[0],decodedArray[1]);


        //Creating header to add the accessToken
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthEntity.getAccessToken());

        //For CrossOrigin and to let browser access header and set header in sessionStorage
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);

        //Creating login response Containing variables as per the loginResponse.

        LoginResponse loginResponse = new LoginResponse()
                .id(customerAuthEntity.getCustomer().getUuid())
                .contactNumber(customerAuthEntity.getCustomer().getContactNumber())
                .emailAddress(customerAuthEntity.getCustomer().getEmail())
                .firstName(customerAuthEntity.getCustomer().getFirstName())
                .lastName(customerAuthEntity.getCustomer().getLastName())
                .message("LOGGED IN SUCCESSFULLY");

        return new ResponseEntity<LoginResponse>(loginResponse,headers,HttpStatus.OK);
    }


    /* This method handles the customer logout ,It takes the Bearer accessToken from authorization in the header and logs out the customer
    and returns a LogoutResponse conatining UUID of customer and the successful message.If error returns the error code with corresponding Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "/logout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> customerLogout (@RequestHeader("authorization")final String authorization)throws AuthorizationFailedException {
        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService logout method which takes accessToken,if data is all right updates customerAuthEntity logout time and returns updated one.
        CustomerAuthEntity customerAuthEntity =  customerService.logout(accessToken);

        //Creating Logout response for the request containing UUID and Successful message.
        LogoutResponse logoutResponse = new LogoutResponse()
                .id(customerAuthEntity.getCustomer().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse,HttpStatus.OK);
    }

    /* This method handles the customer details update request. Takes the request as UpdateCustomerRequest and produces UpdateCustomerResponse containing the details of the updated Customer.
    If error returns the error code with corresponding Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT,path = "",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(@RequestHeader("authorization")final String authorization,@RequestBody(required = false) UpdateCustomerRequest updateCustomerRequest)throws AuthorizationFailedException,UpdateCustomerException{

        //Checking if the request is valid
        utilityProvider.isValidUpdateCustomerRequest(updateCustomerRequest.getFirstName());

        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity  to be updated.
        CustomerEntity toBeUpdatedCustomerEntity = customerService.getCustomer(accessToken);

        //updating the customer entity
        toBeUpdatedCustomerEntity.setFirstName(updateCustomerRequest.getFirstName());
        toBeUpdatedCustomerEntity.setLastName(updateCustomerRequest.getLastName());

        //Calls customerService updateCustomer to persist the updated Entity.
        CustomerEntity updatedCustomerEntity = customerService.updateCustomer(toBeUpdatedCustomerEntity);

        //Creating the UpadteCustomerResponse with updated details.
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .firstName(updatedCustomerEntity.getFirstName())
                .lastName(updatedCustomerEntity.getLastName())
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse,HttpStatus.OK);
    }

    /* This method is used to update password of the customer.It takes updatePasswordRequest and produces updatePasswordResponse containing UUID and the successful message.
    If error returns the error code with corresponding Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT,path = "/password",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(@RequestHeader ("authorization") final String authorization,@RequestBody(required = false) UpdatePasswordRequest updatePasswordRequest)throws AuthorizationFailedException,UpdateCustomerException{

        //Checking if the data received is valid
        utilityProvider.isValidUpdatePasswordRequest(updatePasswordRequest.getOldPassword(),updatePasswordRequest.getNewPassword());

        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        //Creating varibles for passwords
        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity  to be updated.
        CustomerEntity toBeUpdatedCustomerEntity = customerService.getCustomer(accessToken);

        //updating the customer entity
        CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(oldPassword,newPassword,toBeUpdatedCustomerEntity);

        //Creating the UpadtePasswordResponse with updated details.
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse,HttpStatus.OK);
    }

}

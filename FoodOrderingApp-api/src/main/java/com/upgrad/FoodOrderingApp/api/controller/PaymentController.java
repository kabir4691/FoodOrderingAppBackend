package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

// Payment Controller Handles all  the Payment related endpoints

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService; // Handles all the Service Related to Payment.


    /* The method handles get all payment methods fro the order.
    & produces response in PaymentListResponse and returns details of all the payment methods UUID and Name and if error returns error code and error Message.
    */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllPaymentMethods (){

        //Calls getAllPaymentMethods of paymentService tp get all the paymentEntity in DB
        List<PaymentEntity> paymentEntities = paymentService.getAllPaymentMethods();

        //Creating List of Payment Response
        List<PaymentResponse> paymentResponses = new LinkedList<>();
        paymentEntities.forEach(paymentEntity -> {
            PaymentResponse paymentResponse = new PaymentResponse()
                    .paymentName(paymentEntity.getPaymentName())
                    .id(UUID.fromString(paymentEntity.getUuid()));
            paymentResponses.add(paymentResponse);
        });

        //Creating PaymentListResponse by adding list of PaymentResponse to it
        PaymentListResponse paymentListResponse = new PaymentListResponse()
                .paymentMethods(paymentResponses);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);

    }
}

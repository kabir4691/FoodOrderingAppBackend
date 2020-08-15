package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

//This Class is created to access DB with respect to Payment entity

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;


    //To get Payment By UUID from the db
    public PaymentEntity getPaymentByUUID(String paymentId) {
        try{
            PaymentEntity paymentEntity = entityManager.createNamedQuery("getPaymentByUUID",PaymentEntity.class).setParameter("uuid",paymentId).getSingleResult();
            return paymentEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
    //To get All Payment Methods from the db
    public List<PaymentEntity> getAllPaymentMethods() {
        try {
            List<PaymentEntity> paymentEntities =entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();
            return paymentEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}

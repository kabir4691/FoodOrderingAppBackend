package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/** The corresponding DAO class for CustomerEntity */
@Repository
public class CustomerDao {

  @PersistenceContext
  private EntityManager entityManager;

  public CustomerEntity createCustomer(CustomerEntity customerEntity) {
    entityManager.persist(customerEntity);
    return customerEntity;
  }

  public CustomerEntity getCustomerByContactNumber(final String contact_number) {
    try {
      return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class).setParameter("contact_number", contact_number).getSingleResult();
    } catch (NoResultException nre){
      return null;
    }
  }
}

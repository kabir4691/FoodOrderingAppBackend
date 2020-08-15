package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/** The corresponding DAO class for CustomerAuthEntity */
@Repository
public class CustomerAuthDao {

  @PersistenceContext
  private EntityManager entityManager;

  public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity customerAuthEntity) {
    entityManager.persist(customerAuthEntity);
    return customerAuthEntity;
  }
}

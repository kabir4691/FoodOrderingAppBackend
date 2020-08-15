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

  public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken) {
    try {
      return entityManager.createNamedQuery("getCustomerAuthByAccessToken", CustomerAuthEntity.class).setParameter("access_token", accessToken).getSingleResult();
    } catch (NoResultException nre){
      return null;
    }
  }

  public CustomerAuthEntity updateCustomerAuth(CustomerAuthEntity customerAuthEntity){
    entityManager.merge(customerAuthEntity);
    return customerAuthEntity;
  }
}

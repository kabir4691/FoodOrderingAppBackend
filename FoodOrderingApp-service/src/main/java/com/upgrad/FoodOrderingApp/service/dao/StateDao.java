package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//This Class is created to access database with respect to State Entity

@Repository
public class StateDao {


    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get State By UUID no results return null
     * @param uuid
     * @return
     */
    public StateEntity getStateByUuid(String uuid){
        try{
            StateEntity stateEntity = entityManager.createNamedQuery("getStateByUuid",StateEntity.class).setParameter("uuid",uuid).getSingleResult();
            return stateEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Get All States if no results return null
     * @return
     */
    public List<StateEntity> getAllStates(){
        try {
            List<StateEntity> stateEntities = entityManager.createNamedQuery("getAllStates",StateEntity.class).getResultList();
            return stateEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}

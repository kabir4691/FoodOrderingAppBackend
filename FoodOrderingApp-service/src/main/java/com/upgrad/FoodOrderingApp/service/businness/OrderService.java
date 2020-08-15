package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
public class OrderService {


    @Autowired
    OrderDao orderDao; //Handles all data related to the OrdersEntity

    @Autowired
    CouponDao couponDao; //Handles all data related to the CouponEntity

    @Autowired
    OrderItemDao orderItemDao; //Handles all data related to the OrderItemEntity

    @Autowired
    CustomerDao customerDao; //Handles all data related to the CustomerEntity


    /* This method is to get Coupon By CouponName.Takes the couponName  and returns the Coupon Entity.
    If error throws exception with error code and error message.
    */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if(couponName == null||couponName == ""){ //Checking if Coupon Name is Null
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }

        //Calls getCouponByCouponName method of CouponDao.
        CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);
        if(couponEntity == null){ //Checking if couponEntity is Null
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }

        return couponEntity;
    }

    /* This method is to get Coupon By CouponId.Takes the couponUuid  and returns the Coupon Entity.
    If error throws exception with error code and error message.
    */
    public CouponEntity getCouponByCouponId(String couponUuid) throws CouponNotFoundException {

        //Calls getCouponByCouponId method of CouponDao to get coupon entity
        CouponEntity couponEntity = couponDao.getCouponByCouponId(couponUuid);
        if(couponEntity == null){   //Checking if couponEntity is Null
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        }
        return couponEntity;
    }

    /* This method is to saveOrder.Takes the OrdersEntity  and saves it to DB and returns saved the Coupon Entity.
    If error throws exception with error code and error message.
    */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {

        //Calls saveOrder of orderDao to save the Order entity.
        OrdersEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);
        return savedOrderEntity;

    }

    /* This method is to saveOrderItem.Takes the orderItemEntity  and saves it to DB and returns saved the OrderItemEntity.
    If error throws exception with error code and error message.
    */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem (OrderItemEntity orderItemEntity){

        //Calls saveOrderItem of orderItemDao to save the OrderItemEntity.
        OrderItemEntity savedOrderItemEntity = orderItemDao.saveOrderItem(orderItemEntity);
        return savedOrderItemEntity;
    }

    /* This method is to get Orders By Customers.Takes the customerUuid  and returns the list of OrdersEntity .
    If error throws exception with error code and error message.
    */
    public List<OrdersEntity> getOrdersByCustomers(String customerUuid) {

        //calls getCustomerByUuid to get customer from the DB.
        CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerUuid);

        //Calls getOrdersByCustomers or OrderDao to get the past order list of the customer.
        List<OrdersEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    /* This method is to get Order Items By Order.Takes the ordersEntity  and returns the list of OrderItemEntity .
    If error throws exception with error code and error message.
    */
    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity ordersEntity) {

        //Calls getOrderItemsByOrder of orderItemDao to return list of OrderItemEntity corresponding to the order.
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(ordersEntity);
        return orderItemEntities;
    }
}

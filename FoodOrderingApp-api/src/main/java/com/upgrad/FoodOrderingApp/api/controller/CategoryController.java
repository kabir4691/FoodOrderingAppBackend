package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

// Category Controller Handles all  the Category related endpoints

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

     /* The method handles get All Categories request
     & produces response in CategoriesListResponse and returns list of category with details from the db. If error returns error code and error message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories(){

        //Calls get All Categories Ordered By Name method of categoryService to get the list of CategoryEntity.
        List<CategoryEntity> categoryEntities = categoryService.getAllCategoriesOrderedByName();

        if(!categoryEntities.isEmpty()) {  //Checking if the categoryEntities is empty
            //Creating List of  CategoryListResponse for response.
            List<CategoryListResponse> categoryListResponses = new LinkedList<>();
            categoryEntities.forEach(categoryEntity -> { //Looping in for each category Entity in categoryEntities
                //Creating Category List response to add it to the list.
                CategoryListResponse categoryListResponse = new CategoryListResponse()
                        .id(UUID.fromString(categoryEntity.getUuid()))
                        .categoryName(categoryEntity.getCategoryName());
                categoryListResponses.add(categoryListResponse);
            });

            //Creating the CategoriesListResponse and adding list of category list response
            CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoryListResponses);
            return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<CategoriesListResponse>(new CategoriesListResponse(),HttpStatus.OK);
        }
    }

    /* The method handles get Category By Id request and takes the category_id as the path variable.
     & produces response in CategoryDetailsResponse and returns category with details from the db. If error returns error code and error message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable(value = "category_id")final String categoryUuid) throws CategoryNotFoundException {

        //Calls get Category By Id method of categoryService to get the CategoryEntity.
        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryUuid);

        //Creating list of itemEntities corresponding to the categoryEntities.
        List<ItemEntity> itemEntities = categoryEntity.getItems();

        //Creating List of ItemList
        List<ItemList> itemLists = new LinkedList<>();
        itemEntities.forEach(itemEntity -> { //Looping in for each itemEntity in itemEntities
            //Creating ItemList to add to listof Item List
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .price(itemEntity.getPrice())
                    .itemName(itemEntity.getitemName())
                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
            itemLists.add(itemList);
        });

        //Creating CategoryDetailsResponse by adding the itemList and other details.
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .categoryName(categoryEntity.getCategoryName())
                .id(UUID.fromString(categoryEntity.getUuid()))
                .itemList(itemLists);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse,HttpStatus.OK);
    }

}

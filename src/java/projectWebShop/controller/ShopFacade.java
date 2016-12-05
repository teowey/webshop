/*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.controller;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import projectWebShop.model.Inventory;
import projectWebShop.model.ShopBusinessLogic;
import projectWebShop.model.Users;

/**
 *
 * @author Gustav
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless

public class ShopFacade {

    @PersistenceContext(unitName = "projectWebShopPU")
    private EntityManager em;
    
    private List<Inventory> items;
    private ArrayList<Inventory> inventoryList = new ArrayList<>();
    private ArrayList<Inventory> shoppingCart = new ArrayList<>();
    private double sum;
    private ShopBusinessLogic sbl = new ShopBusinessLogic();
    private Inventory buyItem;
    private Users user;
    
    /**
     * Gets all items from the database, puts them in a list and returns the list
     */
    public List<Inventory> browse() {
        items = em.createNamedQuery("Inventory.findAll", Inventory.class).getResultList();
        for (Inventory item : items) {
            if (!inventoryList.contains(item)) {
                inventoryList.add(item);
            }
        }
        return inventoryList;
    }

    /**
     * Creates a new item to put in the shopping cart list and returns the list.
     * Also calls the calcSum method to get the total sum
     */
    public List<Inventory> putInCart(Inventory item, int amountToCart) {
        Inventory itemToCart = new Inventory(item.getItemname(), item.getPrice(), amountToCart);
        shoppingCart.add(itemToCart);
        sum = sbl.calcSum(itemToCart, sum);
        return shoppingCart;
    }

    public double getSum() {
        return sum;
    }
    
    public boolean login(String username, String password){
        user = em.find(Users.class, username);
        if(user == null) {
            return false;
        }
        if(!password.equals(user.getPassword())) {
            return false;
        }
        
        return true;
    }
    public boolean checkBlock(String username) {
        user = em.find(Users.class, username);
        return user.getBlocked();
    }

    public boolean checkAdmin(String username) {
        user = em.find(Users.class, username);
        if (user.getUsername() == "admin") {
            return true;
        } else {
            return false;
        }
    }
    
    public Users register(String username, String password) throws Exception {
        
        Users newUser = em.find(Users.class, username);
        if(newUser != null && newUser.getUsername().equals(username)){
            throw new Exception("User already exists");
        }
        
        user = new Users(username, password, false);
        em.persist(user);
       
        return user;
    }
   
    
    public int getRemaining(Inventory item) {
        Inventory entity = em.find(Inventory.class, item.getItemname());
        return entity.getAmount();
    }
    
    public List<Inventory> buy() {
        for(Inventory item : shoppingCart) {
            buyItem = em.find(Inventory.class, item.getItemname());
            int amount = buyItem.getAmount();
            if(item.getAmount() == amount) {
                em.remove(buyItem);
            }else {
                buyItem.setAmount(amount - item.getAmount());
            }
        }
        sum = 0.0;
        return shoppingCart = null;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.view;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import projectWebShop.controller.ShopFacade;
import projectWebShop.model.Inventory;
import projectWebShop.model.Users;

/**
 *
 * @author Gustav
 */
@Named("shopManager")
@SessionScoped

public class ShopManager implements Serializable {

    @EJB
    private ShopFacade sf;
    private boolean blocked;
    private boolean loggedIn;
    private boolean loggedOut;
    private String username;
    private String password;
    private Exception transactionFailure;
    private List<Inventory> inventory;
    private int amountToCart;
    private int amountToBuy;
    private boolean isBrowsed;
    private Inventory itemToCart;
    private List<Inventory> shoppingCart;
    private double totalSum;
    private Users user;
    private boolean isAdmin;

    /**
     * Next three methods handle exceptions
     */
    private void handleException(Exception e) {
        e.printStackTrace(System.err);
        transactionFailure = e;
    }

    public boolean getSuccess() {
        return transactionFailure == null;
    }

    public Exception getException() {
        return transactionFailure;
    }

    /**
     * Called on log in. Sets the variable "loggedIn" to true if the specified
     * username and password matches a user in the database, false otherwise
     */
    public void login() {
        try {
            // call to controller with username and password
            isAdmin = false;
            loggedIn = false;
            transactionFailure = null;

            blocked = sf.checkBlock(username);
            if (blocked) {
                throw new Exception("User blocked by admin");
            }
            if (sf.checkAdmin(username)) {
                isAdmin = true;
            }
            loggedIn = sf.login(username, password);

            if (loggedIn == false) {
                throw new Exception("Wrong username or password");
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

    public void register() {
        try {
            transactionFailure = null;

            user = sf.register(username, password);

            if (user.getUsername().equals("admin")) {
                isAdmin = true;
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     *
     */
    public void logout() {
        loggedIn = false;
        username = "";
        password = "";
    }

    /**
     *
     * @param user
     * @return
     */
    private boolean blocked(String user) {
        // call to controller to check if the user is blocked or not
        // returns true is the user is in the blocked table, false otherwise

        return true;
    }

    public void browse() {
        transactionFailure = null;
        inventory = sf.browse();
        isBrowsed = true;
    }

    public void putInCart(Inventory item) {
        System.out.println("Amount to add: " + amountToCart);
        System.out.println("In stock: " + item.getAmount());
        transactionFailure = null;
        int inStock = sf.getRemaining(item);
        try {
            if (amountToCart > inStock) {
                throw new IllegalArgumentException("You cannot buy that many, only " + inStock + " left in stock");
            } else {
                shoppingCart = sf.putInCart(item, amountToCart);
            }
        } catch (IllegalArgumentException e) {
            handleException(e);
        }
    }

    public void updateCart() {
        totalSum = sf.getSum();
    }

    public void buy() {
        shoppingCart = sf.buy();
        totalSum = 0.0;
    }

    /**
     * All code below required getters and setters
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setAmountToCart(int amountToCart) {
        this.amountToCart = amountToCart;
    }

    public int getAmountToCart() {
        return amountToCart;
    }

    public void setAmountToBuy(int amountToBuy) {
        this.amountToBuy = amountToBuy;
    }

    public int getAmountToBuy() {
        return amountToBuy;
    }

    public boolean getIsBrowsed() {
        return isBrowsed;
    }

    public Inventory getItemToCart() {
        return itemToCart;
    }

    public List<Inventory> getShoppingCart() {
        return shoppingCart;
    }

    public double getTotalSum() {
        return totalSum;
    }

}

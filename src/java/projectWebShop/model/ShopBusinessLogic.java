/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.model;

/**
 *
 * @author Gustav
 */
public class ShopBusinessLogic {
    
    public ShopBusinessLogic() {
    }
    
    public double calcSum(Inventory item, double sum) {
        double total = sum + item.getAmount()*item.getPrice();
        return total;
    }
}

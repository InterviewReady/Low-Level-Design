//From: https://youtu.be/T-9s9U28KJM

import java.util.HashMap;
import java.util.Map;

public class InventorySystem {

    static Map<String, Product> productMap = new HashMap<>();
    static Map<Location, Unit> locationMap = new HashMap<>();

    public static void addProduct(Product product) {
        productMap.put(product.id, product);
    }

    public static Product getProduct(String product) {
        return productMap.get(product);
    }

    public static void placeUnit(Unit unit) {
        for (Map.Entry<Location, Unit> entry: locationMap.entrySet()){
            // if (SimpleStrategy.works())
            //get lock on entry.getKey()
                if(entry.getValue()==null){
                    unit.locationId = entry.getKey().id;
                }
                //release lock
            }
    }

    public static void removeUnit(Product product) {
        for (Map.Entry<Location, Unit> entry: locationMap.entrySet()){
            // if (SimpleStrategy.works())
            //get lock on entry.getKey()
            if(entry.getValue()!=null && product.id.equals(entry.getValue().productId)){
                locationMap.remove(entry.getKey());
            }
            //release lock
        }
    }

    public static Map<Location, Unit> getShelvesStatus(){
        return locationMap;
    }

    public static void updateStatus(Unit unit, Status status){
        unit.status = status;
    }
}

class SimpleStrategy {

}

class SmartStrategy {

}

class Unit {
    String id;
    String productId;
    String locationId;
    Status status;
}

class Location {
    String id;
    Size size;
}

enum Status {
    INVENTORY, TRANSIT, DELIVERY
}

class Product {
    String id;
    Double price;
    String description;
    double weight;
    Size size;

    public Product(String id, Double price, String description, double weight, Size size) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.weight = weight;
        this.size = size;
    }
}

enum Size {
    S,M,L
}

class User {
    public void addProduct(){
        InventorySystem.addProduct(new Product("", 0d,"",0,Size.L));
    }

    public void executeOrder(Order order){
        for(Map.Entry<Product, Integer> item: order.productCount.entrySet()) {
            for (int i = 0; i < item.getValue(); i++) {
                InventorySystem.removeUnit(item.getKey());
            }
        }
    }
}

class Order {
    Map<Product, Integer> productCount = new HashMap<>();
}

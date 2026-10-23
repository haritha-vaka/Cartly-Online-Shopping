package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.*;

@SpringBootApplication
@RestController
public class OnlineShoppingCartApplication {
    // HASHING: product ID -> product, average O(1) lookup.
    private final Map<Integer, Product> productHash = new HashMap<>();
    private final ShoppingCart cart = new ShoppingCart();

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingCartApplication.class, args);
    }

    public OnlineShoppingCartApplication() {
        seed(101,"Wireless Headphones","Electronics",2499,12,"🎧","Immersive sound for every day.");
        seed(102,"Everyday Sneakers","Fashion",1899,15,"👟","Comfortable shoes for daily adventures.");
        seed(103,"Smart Watch","Electronics",3299,8,"⌚","Track activity and stay connected.");
        seed(104,"Travel Backpack","Accessories",1299,14,"🎒","A roomy companion for work or travel.");
        seed(105,"Coffee Tumbler","Home",499,20,"☕","Keep your favourite drink close.");
        seed(106,"Classic Sunglasses","Fashion",799,18,"🕶️","A timeless look for sunny days.");
        seed(107,"Bluetooth Speaker","Electronics",1599,9,"🔊","Portable sound for your playlist.");
        seed(108,"Desk Lamp","Home",999,11,"💡","A warm glow for your workspace.");
        seed(109,"Fitness Bottle","Accessories",349,24,"🧴","Stay hydrated throughout the day.");
        seed(110,"Smartphone","Electronics",24999,6,"📱","A powerful phone for everyday tasks.");
        seed(111,"Laptop","Electronics",54999,4,"💻","A reliable laptop for study and work.");
        seed(112,"Digital Camera","Electronics",18999,5,"📷","Capture moments in crisp detail.");
        seed(113,"Classic Wristwatch","Accessories",2199,10,"⌚","Simple style with a classic finish.");
        seed(114,"Yoga Mat","Fitness",799,16,"🧘","A comfortable base for your practice.");
        seed(115,"Hardcover Notebook","Stationery",249,30,"📓","Capture ideas, plans and notes.");
        seed(116,"Skincare Set","Beauty",1499,7,"🧴","A simple daily-care essentials set.");
        seed(117,"Running Shoes","Fashion",2799,8,"👟","Supportive shoes for your next run.");
        seed(118,"Ceramic Coffee Mug","Home",399,22,"☕","A ceramic mug for your coffee break.");
    }
    private void seed(int id,String name,String category,double price,int stock,String icon,String description) {
        productHash.put(id,new Product(id,name,category,price,stock,icon,description));
    }

    @GetMapping("/api/products")
    public List<Product> products(@RequestParam(defaultValue="") String q,
                                  @RequestParam(defaultValue="featured") String sort) {
        List<Product> result = new ArrayList<>();
        // LINEAR SEARCH: inspect each product's name, category and description.
        for (Product p : productHash.values()) {
            String searchable = (p.name+" "+p.category+" "+p.description).toLowerCase();
            if (searchable.contains(q.toLowerCase())) result.add(p.copy());
        }
        // Custom MERGE SORT for price sorting.
        if (sort.equals("priceAsc")) mergeSort(result,true);
        else if (sort.equals("priceDesc")) mergeSort(result,false);
        else if (sort.equals("name")) result.sort(Comparator.comparing(p -> p.name.toLowerCase()));
        return result;
    }

    @GetMapping("/api/cart")
    public CartView getCart() { return cart.view(productHash); }

    @PostMapping("/api/cart")
    public CartView add(@RequestBody CartRequest request) {
        if (request == null || request.productId == null || request.quantity == null)
            throw new IllegalArgumentException("Product ID and quantity are required.");
        Product p=productHash.get(request.productId); // HASHMAP lookup
        if (p == null) throw new IllegalArgumentException("Product not found.");
        cart.add(p,request.quantity);
        return cart.view(productHash);
    }

    @PutMapping("/api/cart/{id}")
    public CartView update(@PathVariable int id,@RequestBody CartRequest request) {
        if (request == null || request.quantity == null) throw new IllegalArgumentException("Quantity is required.");
        Product p=productHash.get(id);
        if (p == null) throw new IllegalArgumentException("Product not found.");
        cart.update(id,request.quantity,p.stock);
        return cart.view(productHash);
    }

    @DeleteMapping("/api/cart/{id}")
    public CartView remove(@PathVariable int id) {
        cart.remove(id);
        return cart.view(productHash);
    }

    @DeleteMapping("/api/cart")
    public CartView clear() { cart.clear(); return cart.view(productHash); }

    @PostMapping("/api/checkout")
    public Map<String,Object> checkout() {
        CartView v=cart.view(productHash);
        if (v.items.isEmpty()) throw new IllegalArgumentException("Your cart is empty.");
        Map<String,Object> response=new LinkedHashMap<>();
        response.put("message","Demo order placed successfully!");
        response.put("total",v.total);
        cart.clear();
        response.put("cart",cart.view(productHash));
        return response;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handle(IllegalArgumentException e) { return Map.of("error",e.getMessage()); }

    // MERGE SORT: O(n log n) time, O(n) auxiliary space.
    private void mergeSort(List<Product> a, boolean ascending) {
        if (a.size() <= 1) return;
        int mid=a.size()/2;
        List<Product> left=new ArrayList<>(a.subList(0,mid));
        List<Product> right=new ArrayList<>(a.subList(mid,a.size()));
        mergeSort(left,ascending); mergeSort(right,ascending);
        int i=0,j=0,k=0;
        while(i<left.size() && j<right.size()) {
            boolean useLeft=ascending ? left.get(i).price<=right.get(j).price
                                      : left.get(i).price>=right.get(j).price;
            a.set(k++,useLeft?left.get(i++):right.get(j++));
        }
        while(i<left.size()) a.set(k++,left.get(i++));
        while(j<right.size()) a.set(k++,right.get(j++));
    }

    static class Product {
        public int id; public String name,category,icon,description; public double price; public int stock;
        Product(int id,String name,String category,double price,int stock,String icon,String description) {
            this.id=id;this.name=name;this.category=category;this.price=price;this.stock=stock;this.icon=icon;this.description=description;
        }
        Product copy(){return new Product(id,name,category,price,stock,icon,description);}
    }
    static class CartRequest { public Integer productId; public Integer quantity; }
    static class CartItem {
        public Product product; public int quantity; public double lineTotal;
        CartItem(Product p,int q){product=p.copy();quantity=q;lineTotal=p.price*q;}
    }
    static class CartView {
        public List<CartItem> items=new ArrayList<>();
        public int itemCount; public double subtotal,delivery,total;
    }

    // CUSTOM SINGLY LINKED LIST used as the cart data structure.
    static class CartNode {
        int productId,quantity; CartNode next;
        CartNode(int id,int quantity){this.productId=id;this.quantity=quantity;}
    }
    static class ShoppingCart {
        private CartNode head;

        private CartNode find(int id) {
            CartNode current=head;
            while(current!=null){if(current.productId==id)return current;current=current.next;}
            return null;
        }
        void add(Product p,int quantity) {
            if(quantity<=0)throw new IllegalArgumentException("Quantity must be positive.");
            if(quantity>p.stock)throw new IllegalArgumentException("Only "+p.stock+" in stock.");
            CartNode existing=find(p.id);
            if(existing!=null){
                if(existing.quantity+quantity>p.stock)throw new IllegalArgumentException("Requested quantity exceeds stock.");
                existing.quantity+=quantity;return;
            }
            CartNode fresh=new CartNode(p.id,quantity);
            if(head==null)head=fresh;
            else{CartNode current=head;while(current.next!=null)current=current.next;current.next=fresh;}
        }
        void update(int id,int quantity,int stock) {
            if(quantity<0)throw new IllegalArgumentException("Quantity cannot be negative.");
            if(quantity==0){remove(id);return;}
            if(quantity>stock)throw new IllegalArgumentException("Quantity exceeds stock.");
            CartNode node=find(id);
            if(node==null)throw new IllegalArgumentException("Product is not in your cart.");
            node.quantity=quantity;
        }
        void remove(int id) {
            if(head==null)return;
            if(head.productId==id){head=head.next;return;}
            CartNode current=head;
            while(current.next!=null && current.next.productId!=id)current=current.next;
            if(current.next!=null)current.next=current.next.next;
        }
        CartView view(Map<Integer,Product> productHash) {
            CartView v=new CartView(); CartNode current=head;
            while(current!=null) {
                Product p=productHash.get(current.productId);
                if(p!=null){v.items.add(new CartItem(p,current.quantity));v.itemCount+=current.quantity;v.subtotal+=p.price*current.quantity;}
                current=current.next;
            }
            v.delivery=(v.subtotal==0 || v.subtotal>=2000)?0:99;
            v.total=v.subtotal+v.delivery;
            return v;
        }
        void clear(){head=null;}
    }
}

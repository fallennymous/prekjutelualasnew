package com.example.fallennymous.prekjutelulas.Model;

import java.io.Serializable;

/**
 * Created by fallennymous on 11/07/2018.
 */

public class Food {
    private String Name, Image, Description, MenuId, Price, Discount;


    public Food() {
    }

    public Food(String name, String image, String descripsi, String price, String discount, String menuId) {
        Name = name;
        Image = image;
        Description = descripsi;
        Price = price;
        Discount = discount;
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}


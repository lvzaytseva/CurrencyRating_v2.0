package com.example.currencyratingv20.data;

import java.util.Objects;

import androidx.annotation.Nullable;

public class Currency implements Comparable<Currency> {
    String name;
    String dynamics;
    double price;

    public Currency()
    {
        this.name = "";
        this.dynamics = "N/A";
        this.price = 0;
    }
    public Currency(String name, double price)
    {
        this.name = name;
        this.price = price;
        this.dynamics = "N/A";
    }
    public Currency(String name, double price, String dynamics)
    {
        this.name = name;
        this.price = price;
        this.dynamics = dynamics;
    }
    public Currency (Currency currency)
    {
        this.name = currency.getName();
        this.price = currency.getPrice();
        this.dynamics = currency.getDynamics();
    }

    public String getDynamics()
    {
        return dynamics;
    }

    public String getName()
    {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setDynamics(String dynamics) {
        this.dynamics = dynamics;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean equalCurr(Currency currency)
    {
        return this.name.equals(currency.name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        Currency temp = (Currency) obj;
            return this.name.equals(temp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public int compareTo(Currency o) {
        if (o.getDynamics().equals("N/A"))
            return -1;
        else if (this.getDynamics().equals("N/A"))
                return 1;
        else {
            int compare = Double.compare(Double.parseDouble(o.getDynamics()), Double.parseDouble(this.getDynamics()));
            return compare;
        }
            }



}
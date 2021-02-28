package com.example.currencyratingv20.data;

import java.math.BigDecimal;
import java.util.Objects;

import androidx.annotation.Nullable;

public class Currency {
    private final int id;
    private final String name;
    private BigDecimal price;

    public Currency(int id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Currency temp = (Currency) obj;
            return this.id == temp.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
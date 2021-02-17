package com.example.currencyratingv20.data;

import java.math.BigDecimal;
import java.util.Objects;

import androidx.annotation.Nullable;

public class Currency implements Comparable<Currency> {
    private final String name;
    private BigDecimal dynamics;
    private BigDecimal price;

    public Currency(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        this.dynamics = null;
    }

    public BigDecimal getDynamics() {
        return dynamics;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setDynamics(BigDecimal dynamics) {
        this.dynamics = dynamics;
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
            return this.name.equals(temp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public int compareTo(Currency o) {
        if (o.getDynamics() == null)
            return -1;
        else if (this.getDynamics() == null)
                return 1;
        else {
            return o.getDynamics().compareTo(this.getDynamics());
        }
            }
}
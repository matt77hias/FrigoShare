package com.frigoshare.utils;

public interface Filter<T> {

    public boolean accept(T t);
}

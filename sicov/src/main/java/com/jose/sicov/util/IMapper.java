package com.jose.sicov.util;

public interface IMapper<T> {

    T getDto();
    void setData(T t);
}

package org.lgangloff.web.api;

import lombok.Data;

@Data
public class ApiData<T> extends ApiDataBase {

    private T entity;

    public ApiData(T entity){
        this.entity = entity;
    }
}

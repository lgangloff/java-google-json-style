package org.lgangloff.web.api;

import java.util.List;

import lombok.Data;

@Data
public class ApiDataCollection<T> extends ApiDataBase{
    
    private Class<T> itemClass;
    
    private List<T> items;

    public ApiDataCollection(Class<T> itemClass, List<T> items){
        this.items = items;
        this.itemClass = itemClass;
    }
}

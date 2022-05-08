package org.lgangloff.web.serializer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.lgangloff.web.api.ApiPagination;
import org.lgangloff.web.api.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

@Component
public class ApiContext {

    @Autowired
    private HttpServletRequest request;


    public Set<String> findFieldsToRender(Object entity){
        return findFieldsToRender(entity.getClass());
    }
    public Set<String> findFieldsToRender(Class<?> clazz){
        Set<String> fields = new HashSet<>();
        fields.add("id");
        Collections.addAll(
            fields,
            getDefaultOrQueryParam("fields", clazz, JSONData::fields).split(",")
        );
        return fields;
    }
    public ApiPagination paginateItems(Class<?> itemClass, List<?> items) {
        ApiPagination p = new ApiPagination();
        p.setItemsPerPage(  getDefaultOrQueryParamAsInt("itemsPerPage", itemClass, JSONData::itemsPerPage));
        p.setStartIndex(    getDefaultOrQueryParamAsInt("startIndex", () -> 1));
        p.paginate(items == null ? Collections.emptyList() : items);
        return p;
    }

    private int getDefaultOrQueryParamAsInt(String parameterName, Supplier<Integer> defaut) {
        String queryParamValue = request.getParameter(parameterName);
        return parsePositiveInt(queryParamValue, defaut.get());
    }
    private int getDefaultOrQueryParamAsInt(String parameterName, Class<?> c, Function<JSONData, Integer> method){
        return getDefaultOrQueryParamAsInt(parameterName, ()-> defaultValue(c, method));
    }
    private String getDefaultOrQueryParam(String parameterName, Class<?> c, Function<JSONData, String> method){
        String queryParamValue = request.getParameter(parameterName);
        return ObjectUtils.isEmpty(queryParamValue) ? defaultValue(c, method) : queryParamValue;
    }

    private <T> T defaultValue(Class<?> c, Function<JSONData, T> method){ 
        JSONData defaultData = c.getAnnotation(JSONData.class);
        return method.apply(defaultData);
    }

    private int parsePositiveInt(String s, int defaut){
        int value;
        try{
            value = Integer.parseInt(s);
        }
        catch(NumberFormatException e){
            value = defaut;
        }
        return value <= 0 ? defaut : value;
    }
}

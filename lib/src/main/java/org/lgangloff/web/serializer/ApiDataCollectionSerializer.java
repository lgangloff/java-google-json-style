package org.lgangloff.web.serializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

import org.lgangloff.web.api.ApiDataCollection;
import org.lgangloff.web.api.ApiPagination;
import org.lgangloff.web.api.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class ApiDataCollectionSerializer extends JsonSerializer<ApiDataCollection<?>> {

    @Autowired
    private ApiContext apiContext;

    @Override
    public Class<ApiDataCollection<?>> handledType() {
        return (Class) ApiDataCollection.class;
    }

    @Override
    public void serialize(ApiDataCollection<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        String kind = value.getItemClass().getAnnotation(JSONData.class).kind();
        Set<String> fields = apiContext.findFieldsToRender(value.getItemClass());
        gen.writeStartObject();
        gen.writeStringField("kind", kind);
        gen.writeStringField("fields", fields.stream().collect(Collectors.joining(",")));
     
        List<?> items = value.getItems();
        ApiPagination paging = apiContext.paginateItems(value.getItemClass(), items);
        
        gen.writeNumberField("currentItemCount", paging.getCurrentItemCount());
        gen.writeNumberField("itemsPerPage", paging.getItemsPerPage());
        gen.writeNumberField("startIndex", paging.getStartIndex());
        gen.writeNumberField("totalItems", paging.getTotalItems());
        gen.writeNumberField("pageIndex", paging.getPageIndex());
        gen.writeNumberField("totalPages", paging.getTotalPages());

        gen.writeArrayFieldStart("items");
        JsonSerializer<Object> entitySerializer = getSerializerOfEntity(value.getItemClass(), provider);
        for (Object item : paging.getPaginateItems()) {
            gen.writeStartObject();
            entitySerializer.serialize(item, gen, provider);
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeEndObject();
        
    }

    private JsonSerializer<Object> getSerializerOfEntity(Class<?> clazz, SerializerProvider provider) throws JsonMappingException {
        JavaType javaType = provider.constructType(clazz);
        BeanDescription beanDesc = provider.getConfig().introspect(javaType);
        JsonSerializer<Object> serializer = BeanSerializerFactory.instance
                .findBeanOrAddOnSerializer(provider, javaType, beanDesc, false)
                .unwrappingSerializer(null);
        serializer = (JsonSerializer<Object>) serializer.withFilterId("apiFilterFields");
        return serializer;
    }


}

package org.lgangloff.web.serializer;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

import org.lgangloff.web.api.ApiData;
import org.lgangloff.web.api.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiDataSerializer extends JsonSerializer<ApiData<?>> {

    @Autowired
    private ApiContext apiContext;


    @Override
    public Class<ApiData<?>> handledType() {
        return (Class) ApiData.class;
    }

    @Override
    public void serialize(ApiData<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        String kind = value.getEntity().getClass().getAnnotation(JSONData.class).kind();
        Set<String> fields = apiContext.findFieldsToRender(value.getEntity());
        gen.writeStartObject();
        gen.writeStringField("kind", kind);
        gen.writeStringField("fields", fields.stream().collect(Collectors.joining(",")));
     
        JsonSerializer<Object> entitySerializer = getSerializerOfEntity(value.getEntity(), provider);
        entitySerializer.serialize(value.getEntity(), gen, provider);
                
        gen.writeEndObject();
        
    }

    private JsonSerializer<Object> getSerializerOfEntity(Object entity, SerializerProvider provider) throws JsonMappingException {
        JavaType javaType = provider.constructType(entity.getClass());
        BeanDescription beanDesc = provider.getConfig().introspect(javaType);
        JsonSerializer<Object> serializer = BeanSerializerFactory.instance
                .findBeanOrAddOnSerializer(provider, javaType, beanDesc, false)
                .unwrappingSerializer(null);
        serializer = (JsonSerializer<Object>) serializer.withFilterId("apiFilterFields");
        return serializer;
    }


}

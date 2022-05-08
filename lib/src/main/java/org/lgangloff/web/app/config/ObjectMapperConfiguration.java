package org.lgangloff.web.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.lgangloff.web.serializer.ApiContext;
import org.lgangloff.web.serializer.ApiDataCollectionSerializer;
import org.lgangloff.web.serializer.ApiDataSerializer;
import org.lgangloff.web.serializer.ApiFilterFields;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackageClasses = ApiContext.class)
class ObjectMapperConfiguration {

    @Primary
    @Bean
    public ObjectMapper objectMapper(FilterProvider filterProvider, 
        ApiDataSerializer apiDataSerializer, 
        ApiDataCollectionSerializer apiDataCollectionSerializer) {
        
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addSerializer(apiDataSerializer);
        module.addSerializer(apiDataCollectionSerializer);
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());

        

        return mapper
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .setFilterProvider(filterProvider)
          .registerModule(module);
    }

    @Bean
    public FilterProvider filterProvider(ApiFilterFields apiFilterFields){
        SimpleFilterProvider provider = new SimpleFilterProvider();

        provider.addFilter("apiFilterFields", apiFilterFields);

        return provider;
    }
}

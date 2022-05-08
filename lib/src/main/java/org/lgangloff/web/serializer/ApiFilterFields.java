package org.lgangloff.web.serializer;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiFilterFields extends SimpleBeanPropertyFilter {

   @Autowired
   private ApiContext apiContext;

   @Override
   public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer)
         throws Exception {
      if (include(writer)) {
         Set<String> fields = apiContext.findFieldsToRender(pojo);
         
         if (fields == null || fields.contains(writer.getName()) ) {
            writer.serializeAsField(pojo, gen, prov);
         }
      } else if (!gen.canOmitFields()) { // since 2.3
         writer.serializeAsOmittedField(pojo, gen, prov);
      }
   }
}

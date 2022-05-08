package org.lgangloff.web.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.lgangloff.web.api.ApiResponse;
import org.lgangloff.web.api.ApiResponseCollection;
import org.lgangloff.web.api.JSONData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;


@RestController
public class GreetingController {
    

	@GetMapping("/greeting")
	public ApiResponse<Greeting> greeting(
        @RequestParam(value = "name", defaultValue = "World") String name) {

            
        Greeting entity = newGreeting(0);

		return new ApiResponse<>("1.0", entity);
	}


	@GetMapping("/greeting/all")
	public ApiResponseCollection<Greeting> greetingList(
        @RequestParam(value = "name", defaultValue = "World") String name) {

        
        List<Greeting> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(newGreeting(i));
        }

		return new ApiResponseCollection<>("1.0", Greeting.class, items);
	}


    private Greeting newGreeting(int index) {
        Greeting entity = new Greeting();
        entity.id = UUID.randomUUID().toString();
        entity.i = index;
        entity.age = 10;
        entity.lastName = "Dupont";
        entity.name = "Jean";
        entity.date = new Date(System.currentTimeMillis()-((int)Math.random()*10000));
        return entity;
    }

    @JSONData(kind="mydto", fields="id,name", itemsPerPage = 20)
    @Data
    class Greeting{
        String id;
        int i;
        String name;
        String lastName;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Date date;
        int age;
        String nullProp;
    }
}

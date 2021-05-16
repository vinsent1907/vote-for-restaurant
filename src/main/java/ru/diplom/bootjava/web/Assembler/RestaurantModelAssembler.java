package ru.diplom.bootjava.web.Assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.diplom.bootjava.model.Restaurant;
import ru.diplom.bootjava.web.RestaurantController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RestaurantModelAssembler implements RepresentationModelAssembler<Restaurant, EntityModel<Restaurant>> {
    @Override
    public EntityModel<Restaurant> toModel(Restaurant restaurant) {
        return EntityModel.of(restaurant,
                linkTo(methodOn(RestaurantController.class).oneToday(restaurant.getId())).withSelfRel(),
                linkTo(methodOn(RestaurantController.class).allToday()).withRel("restaurants"));
    }
}

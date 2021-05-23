package ru.diplom.bootjava.web.Assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.diplom.bootjava.model.Menu;
import ru.diplom.bootjava.web.RestaurantController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class MenuModelAssembler implements RepresentationModelAssembler<Menu, EntityModel<Menu>> {
    @Override
    public EntityModel<Menu> toModel(Menu menu) {
        return EntityModel.of(menu, Link.of(linkTo(RestaurantController.class) + "/" + menu.getRestaurant().getId()
                + "/" + "menu" + "/" + menu.getId(), "self"));
    }
}

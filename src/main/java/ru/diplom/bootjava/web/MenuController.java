package ru.diplom.bootjava.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diplom.bootjava.web.Assembler.MenuModelAssembler;
import ru.diplom.bootjava.error.NotFoundException;
import ru.diplom.bootjava.model.Menu;
import ru.diplom.bootjava.repository.MenuRepository;
import ru.diplom.bootjava.repository.RestaurantRepository;
import ru.diplom.bootjava.util.ValidationUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/restaurants/{restaurantId}/menus")
@AllArgsConstructor
@Slf4j
@Tag(name = "Menu Controller 2end point")
public class MenuController {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuModelAssembler assembler;

    @GetMapping(value = "/{menuId}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Menu> get(@PathVariable int restaurantId, @PathVariable int menuId) {
        log.info("get menu id = {}", menuId);
        Menu menu = menuRepository.findByRestaurantIdAndId(restaurantId, menuId)
                .orElseThrow(() -> new NotFoundException("For restaurant id = " + restaurantId +
                        " menu with id = " + menuId + " not found"));

        return assembler.toModel(menu);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Menu>> getAll(@PathVariable int restaurantId) {
        log.info("get all menu for restaurant id = {}", restaurantId);
        List<EntityModel<Menu>> votes =
                menuRepository.findAllByRestaurantIdOrderByDescriptionDesc(restaurantId)
                        .stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList());

        return CollectionModel.of(votes,
                linkTo(methodOn(MenuController.class).getAll(restaurantId)).withSelfRel());
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<Menu> delete(@PathVariable int restaurantId, @PathVariable int menuId) {
        log.info("delete menu id = {}", menuId);
        menuRepository.findByRestaurantIdAndId(restaurantId, menuId)
                .orElseThrow(() -> new NotFoundException("Menu with id = " + menuId + " not found"));

        menuRepository.deleteById(menuId);
        return ResponseEntity.noContent().build();

    }

    @PostMapping(value = "/created", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Menu>> create(@Valid @RequestBody Menu menu, @PathVariable int restaurantId) {
        log.info("create {}", menu);
        ValidationUtil.checkNew(menu);
        menu.setRestaurant(restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant with id = " + restaurantId + " not found")));
        Menu newMenu = menuRepository.save(menu);
        EntityModel<Menu> entityModel = assembler.toModel(newMenu);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping(value = "/edit/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Menu>> update(@PathVariable int restaurantId, @PathVariable int menuId,
                                                   @Valid @RequestBody Menu menu) {
        log.info("update menu {} id = {}", menu, menuId);
        Menu oldMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu with id = " + menuId + " not found"));
        ValidationUtil.assureIdConsistent(menu, oldMenu.id());
        menu.setRestaurant(restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant with id = " + restaurantId + " not found")));
        EntityModel<Menu> entityModel = assembler.toModel(menuRepository.save(menu));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}

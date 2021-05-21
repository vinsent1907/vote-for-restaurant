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
import ru.diplom.bootjava.error.NotFoundException;
import ru.diplom.bootjava.model.Restaurant;
import ru.diplom.bootjava.repository.RestaurantRepository;
import ru.diplom.bootjava.util.ValidationUtil;
import ru.diplom.bootjava.web.Assembler.RestaurantModelAssembler;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/restaurants")
@AllArgsConstructor
@Slf4j
@Tag(name = "Restaurant Controller User")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantModelAssembler assembler;

    @GetMapping(value = "/{restaurantId}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Restaurant> oneToday(@PathVariable Integer restaurantId) {
        log.info("get {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findWithMenuByIdAndDate(restaurantId, LocalDate.now())
                .orElseThrow(() -> new NotFoundException("Today restaurant with id = " + restaurantId + " not found"));

        return assembler.toModel(restaurant);
    }

    @GetMapping(value = "/today", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Restaurant>> allToday() {
        log.info("get all restaurant today");

        List<EntityModel<Restaurant>> restaurants =
                restaurantRepository.findAllWithMenuByDate(LocalDate.now())
                        .stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(restaurants,
                linkTo(methodOn(RestaurantController.class).allToday()).withSelfRel());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Restaurant> delete(@PathVariable Integer id) {
        log.info("delete {}", id);
        restaurantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/created", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Restaurant>> created(@Valid @RequestBody Restaurant restaurant) {
        log.info("created {}", restaurant);
        ValidationUtil.checkNew(restaurant);
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        EntityModel<Restaurant> entityModel = assembler.toModel(newRestaurant);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Restaurant>> update(@PathVariable Integer id,
                                                          @Valid @RequestBody Restaurant restaurant) {
        log.info("update {}", restaurant);
        Restaurant oldRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant with id = " + id + " not found"));

        ValidationUtil.assureIdConsistent(restaurant, oldRestaurant.id());
        if (restaurant.getMenu() == null) restaurant.setMenu(oldRestaurant.getMenu());

        EntityModel<Restaurant> entityModel = assembler.toModel(restaurantRepository.save(restaurant));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}

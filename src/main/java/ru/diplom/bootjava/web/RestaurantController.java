package ru.diplom.bootjava.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.diplom.bootjava.error.NotFoundException;
import ru.diplom.bootjava.model.Restaurant;
import ru.diplom.bootjava.repository.RestaurantRepository;
import ru.diplom.bootjava.web.Assembler.RestaurantModelAssembler;

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

    @GetMapping(value = "/today/{restaurantId}", produces = MediaTypes.HAL_JSON_VALUE)
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

}

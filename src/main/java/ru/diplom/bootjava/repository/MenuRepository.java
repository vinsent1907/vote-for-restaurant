package ru.diplom.bootjava.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.diplom.bootjava.model.Menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Tag(name = "Menu Controller")
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @RestResource(rel = "by-restaurantId", path = "by-restaurantId")
    List<Menu> findAllByRestaurantIdOrderByPriceDesc(int restaurantId);

    @RestResource(rel = "by-name", path = "by-name")
    List<Menu> findAllByRestaurantNameContainingIgnoreCaseOrderByPriceDesc(String name);

    @RestResource(rel = "by-restaurantId&date", path = "by-restaurantId&date")
    List<Menu> findAllByRestaurantIdAndDateOrderByPriceDesc(int restaurantId, @RequestParam
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date);

    @RestResource(rel = "by-date", path = "by-date")
    List<Menu> findAllByDateOrderByRestaurantDesc(@RequestParam @DateTimeFormat
            (iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @RestResource(rel = "all-by-restaurantId", path = "all-by-restaurantId")
    List<Menu> findAllByRestaurantIdOrderByDescriptionDesc(int restaurantId);

    @RestResource(rel = "by-restaurantId&id", path = "by-restaurantId&id")
    Optional<Menu> findByRestaurantIdAndId(int restaurantId, int id);
}


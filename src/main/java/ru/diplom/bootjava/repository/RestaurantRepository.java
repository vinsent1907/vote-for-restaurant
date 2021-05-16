package ru.diplom.bootjava.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import ru.diplom.bootjava.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Tag(name = "Restaurant Controller")
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @RestResource(rel = "all-sort", path = "all-sort")
    List<Restaurant> findAllByOrderByNameAsc();

    @RestResource(rel = "by-date", path = "by-date")
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.menu m WHERE m.date=:date ORDER BY r.name")
    List<Restaurant> findAllWithMenuAndDate(LocalDate date);

    @RestResource(rel = "by-id&date", path = "by-id&date")
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN FETCH r.menu m WHERE r.id=:restaurantId  AND m.date=:date ORDER BY r.name")
    Optional<Restaurant> findByIdWithMenuAndDate(Integer restaurantId, LocalDate date);

}

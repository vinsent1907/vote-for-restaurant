package ru.diplom.bootjava.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import ru.diplom.bootjava.model.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Tag(name = "Vote Controller")
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @RestResource(rel = "by-restaurantId", path = "by-restaurantId")
    List<Vote> findAllByRestaurantIdOrderByDatesDesc(int restaurantId);

    @RestResource(rel = "by-date", path = "by-date")
    List<Vote> findAllByDatesOrderByRestaurantIdAsc(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @RestResource(rel = "by-date&userId", path = "by-date&userId")
    Optional<Vote> findByDatesAndUserId(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, int userId);

    @RestResource(rel = "total-by-date&restaurantId", path = "total-by-date&restaurantId")
    long countByDatesAndRestaurantId(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, int restaurantId);
}
package ru.diplom.bootjava.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.diplom.bootjava.AuthUser;
import ru.diplom.bootjava.error.NotFoundException;
import ru.diplom.bootjava.model.Restaurant;
import ru.diplom.bootjava.model.Vote;
import ru.diplom.bootjava.repository.RestaurantRepository;
import ru.diplom.bootjava.repository.VoteRepository;
import ru.diplom.bootjava.to.VoteTo;
import ru.diplom.bootjava.util.VoteUtil;
import ru.diplom.bootjava.web.Assembler.VoteModelAssembler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ru.diplom.bootjava.util.ValidationUtil.checkNull;
import static ru.diplom.bootjava.util.ValidationUtil.checkingPossibilityVoting;
import static ru.diplom.bootjava.util.VoteUtil.asTo;

@RestController
@RequestMapping(value = "/api/votes", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Tag(name = "Vote Controller User")
public class VoteController {

    private static final LocalTime STOP_TIME = LocalTime.of(11, 0);
    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final VoteModelAssembler assembler;

    @PostMapping("/vote")
    public ResponseEntity<EntityModel<VoteTo>> vote(@AuthenticationPrincipal AuthUser authUser,
                                                    @RequestParam int restaurantId) {
        log.info("vote {} for the {} ", authUser, restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant with id = " + restaurantId + " not found"));

        int userId = checkNull(authUser.getUser().getId());

        Vote vote = voteRepository.findByDatesAndUserId(LocalDate.now(), userId)
                .orElse(new Vote(LocalDate.now(), authUser.getUser(), restaurant));

        vote.setRestaurant(restaurant);
        EntityModel<VoteTo> entityModel = assembler.toModel(asTo(voteRepository.save(vote)));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/vote")
    public ResponseEntity<EntityModel<VoteTo>> update(@AuthenticationPrincipal AuthUser authUser,
                                                    @RequestParam int restaurantId) {
        log.info("vote {} for the {} ", authUser, restaurantId);
        checkingPossibilityVoting(STOP_TIME);
       return vote(authUser, restaurantId);
    }


    @GetMapping(value = "/today", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<VoteTo>> getAllToday() {
        log.info("get all vote");
        List<EntityModel<VoteTo>> votes =
                voteRepository.findAllByDatesOrderByRestaurantIdAsc(LocalDate.now())
                        .stream()
                        .map(VoteUtil::asTo)
                        .map(assembler::toModel)
                        .collect(Collectors.toList());

        return CollectionModel.of(votes,
                linkTo(methodOn(VoteController.class).getAllToday()).withSelfRel());
    }

    @GetMapping(value = "/vote", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<VoteTo> getCurrentToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get vote {}", authUser);
        int userId = checkNull(authUser.getUser().getId());
        Vote vote = voteRepository.findByDatesAndUserId(LocalDate.now(), userId)
                .orElseThrow(() -> new NotFoundException("You did not vote today"));
        return assembler.toModel(asTo(vote));
    }
}

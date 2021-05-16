package ru.diplom.bootjava.web.Assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.diplom.bootjava.to.VoteTo;
import ru.diplom.bootjava.web.VoteController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VoteModelAssembler implements RepresentationModelAssembler<VoteTo, EntityModel<VoteTo>> {
    @Override
    public EntityModel<VoteTo> toModel(VoteTo voteTo) {
        return EntityModel.of(voteTo,
                Link.of(linkTo(VoteController.class) + "/vote", "self"),
                linkTo(methodOn(VoteController.class).getAllToday()).withRel("votes"));
    }
}

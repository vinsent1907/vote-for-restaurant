package ru.diplom.bootjava.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.diplom.bootjava.AuthUser;
import ru.diplom.bootjava.web.Assembler.AccountModelAssembler;
import ru.diplom.bootjava.model.Role;
import ru.diplom.bootjava.model.User;
import ru.diplom.bootjava.repository.UserRepository;
import ru.diplom.bootjava.util.ValidationUtil;

import javax.validation.Valid;
import java.util.EnumSet;

/**
 * Do not use {@link org.springframework.data.rest.webmvc.RepositoryRestController (BasePathAwareController}
 * Bugs:
 * NPE with http://localhost:8080/api/account<br>
 * <a href="https://github.com/spring-projects/spring-hateoas/issues/434">data.rest.base-path missed in HAL links</a><br>
 * <a href="https://jira.spring.io/browse/DATAREST-748">Two endpoints created</a>
 * <p>
 * RequestMapping("/${spring.data.rest.`basePath`}/account") give "Not enough variable values"
 */
@RestController
@RequestMapping(AccountController.URL)
@AllArgsConstructor
@Slf4j
@Tag(name = "Account Controller")
public class AccountController {
    static final String URL = "/api/account";

    private final UserRepository userRepository;
    private final AccountModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<User> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get {}", authUser);
        return assembler.toModel(authUser.getUser());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("delete {}", authUser);
        userRepository.deleteById(authUser.id());
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<EntityModel<User>> register(@Valid @RequestBody User user) {
        log.info("register {}", user);
        ValidationUtil.checkNew(user);
        user.setRoles(EnumSet.of(Role.USER));
        EntityModel<User> entityModel = assembler.toModel(userRepository.save(user));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<User>> update(@Valid @RequestBody User user, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} to {}", authUser, user);
        User oldUser = authUser.getUser();
        ValidationUtil.assureIdConsistent(user, oldUser.id());
        user.setRoles(oldUser.getRoles());
        if (user.getPassword() == null) user.setPassword(oldUser.getPassword());
        User updatedUser = userRepository.save(user);

        EntityModel<User> entityModel = assembler.toModel(updatedUser);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}

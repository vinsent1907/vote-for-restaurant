package ru.diplom.bootjava.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.diplom.bootjava.model.User;
import ru.diplom.bootjava.util.JsonUtil;
import ru.diplom.bootjava.UserTestUtil;
import ru.diplom.bootjava.repository.UserRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.diplom.bootjava.UserTestUtil.*;

class AccountControllerTest extends AbstractControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(AccountController.URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonMatcher(user, UserTestUtil::assertEquals));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(AccountController.URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(AccountController.URL))
                .andExpect(status().isNoContent());
        Assertions.assertFalse(userRepository.findById(USER_ID).isPresent());
        Assertions.assertTrue(userRepository.findById(ADMIN_ID).isPresent());
    }

    @Test
    void register() throws Exception {
        User newUser = UserTestUtil.getNew();
        User registered = asUser(perform(MockMvcRequestBuilders.post(AccountController.URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUser)))
                .andExpect(status().isCreated()).andReturn());
        int newId = registered.id();
        newUser.setId(newId);
        UserTestUtil.assertEquals(registered, newUser);
        UserTestUtil.assertEquals(registered, userRepository.findById(newId).orElseThrow());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        User updated = UserTestUtil.getUpdated();
        perform(MockMvcRequestBuilders.put(AccountController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isCreated());
        UserTestUtil.assertEquals(updated, userRepository.findById(USER_ID).orElseThrow());
    }
}
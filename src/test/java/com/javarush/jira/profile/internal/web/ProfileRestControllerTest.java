package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.MatcherFactory;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = ProfileRestController.REST_URL;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void getProfileGuest() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(GUEST_PROFILE_EMPTY_TO));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getProfileUser() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(USER_PROFILE_TO));
    }

    @Test
    void getProfileUnauthorized() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileNotFoundById() throws Exception {
        perform(put(REST_URL + "/99999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateInvalid() throws Exception {
        ProfileTo invalidProfileTo = getInvalidTo();
        invalidProfileTo.setId(USER_ID);
        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidProfileTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}
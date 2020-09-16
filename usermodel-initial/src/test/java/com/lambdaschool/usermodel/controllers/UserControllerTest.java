package com.lambdaschool.usermodel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.services.HelperFunctions;
import com.lambdaschool.usermodel.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private Role r1;
    private Role r2;
    private List<User> users;

    @Before
    public void setUp() throws Exception {

        users = new ArrayList<>();
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-US"),
                new RandomService());
        Faker nameFaker = new Faker(new Locale("en-US"));
        r1 = new Role("Admin");
        r1.setRoleid(1);
        r2 = new Role("User");
        r2.setRoleid(2);
        for (int i = 0; i < 25; i++)
        {
            new User();
            User fakeUser;

            fakeUser = new User(nameFaker.name()
                    .username() + i,
                    "password",
                    nameFaker.internet()
                            .emailAddress() + i);
            fakeUser.getRoles()
                    .add(new UserRoles(fakeUser, r2));
            fakeUser.getUseremails()
                    .add(new Useremail(fakeUser,
                            fakeValuesService.bothify("????##@gmail.com")));
            fakeUser.setUserid(i);
            users.add(fakeUser);
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void a_listAllUsers() throws Exception{
        var apiUrl = "/users/users";
        Mockito.when(userService.findAll()).thenReturn(users);

        var builder = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(builder).andReturn();
        var jsonResult = result.getResponse().getContentAsString();

        var usersAsJson = new ObjectMapper().writeValueAsString(users);
        assertEquals(jsonResult, usersAsJson);
    }

    @Test
    public void b_getUserById() throws Exception{
        var apiUrl = "/users/user/10";
        Mockito.when(userService.findUserById(10)).thenReturn(users.get(0));

        var builder = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(builder).andReturn();
        var jsonResult = result.getResponse().getContentAsString();

        var usersAsJson = new ObjectMapper().writeValueAsString(users.get(0));
        assertEquals(jsonResult, usersAsJson);
    }

    @Test
    public void c_getUserByName() throws Exception{
        var apiUrl = "/users/user/name/dudeperson";
        Mockito.when(userService.findByName("dudeperson")).thenReturn(users.get(0));

        var builder = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(builder).andReturn();
        var jsonResult = result.getResponse().getContentAsString();

        var userAsJson = new ObjectMapper().writeValueAsString(users.get(0));
        assertEquals(jsonResult, userAsJson);
    }

    @Test
    public void d_getUserLikeName() throws Exception{
        var apiUrl = "/users/user/name/like/dude";
        Mockito.when(userService.findByNameContaining("dude")).thenReturn(users);
        var builder = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(builder).andReturn();
        var jsonResult = result.getResponse().getContentAsString();

        var usersAsJson = new ObjectMapper().writeValueAsString(users);
        assertEquals(jsonResult, usersAsJson);
    }

    @Test
    public void e_addNewUser() throws Exception{
        var apiUrl = "/users/user";

        User newUser = new User();
        newUser.setUserid(0);
        newUser.setUsername("test");
        newUser.setPassword("test1234");
        newUser.setPrimaryemail("dudeperson@test.com");
        newUser.getRoles().add(new UserRoles(newUser, r1));
        newUser.getRoles().add(new UserRoles(newUser, r2));
        newUser.getUseremails().add(new Useremail(newUser, "test2@test.com"));
        var newUserAsJson = new ObjectMapper().writeValueAsString(newUser);
        Mockito.when(userService.save(any(User.class))).thenReturn(newUser);
        var builder = MockMvcRequestBuilders.post(apiUrl).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserAsJson);
        mockMvc.perform(builder).andExpect(status().isCreated());
    }

    @Test
    public void f_updateFullUser() {
    }

    @Test
    public void g_updateUser() {
    }

    @Test
    public void h_deleteUserById() throws Exception{
        var apiUrl = "/users/user/1";
        var builder = MockMvcRequestBuilders.delete(apiUrl).accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isOk());
    }
}
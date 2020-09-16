package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplication;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired RoleService roleService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void a_findAll() {
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void b_findUserById() {
        List<User> users = userService.findAll();
        Random random = new Random();
        var toExpect = users.get(random.nextInt(users.size()));
        User dataUser = userService.findUserById(toExpect.getUserid());
        assertEquals(toExpect.getUsername(), dataUser.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void c_findUserByIdNotFound() {
        assertEquals("", userService.findUserById(50000).getUsername());
    }

    @Test
    public void d_findByNameContaining() {
        assertEquals(1, userService.findByNameContaining("cinnamo").size());
    }

    @Test
    public void e_findByName() {
        assertEquals("puttat", userService.findByName("puttat").getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void f_findByNameNotAvailable() {
        assertEquals("", userService.findByName("lskdjflskjdflkjsdlkfj"));
    }

    @Test
    public void g_save() {
        User newUser = new User();
        newUser.setPassword("test");
        newUser.setUsername("test user");
        newUser.setPrimaryemail("test@user.com");
        List<Role> roles = roleService.findAll();
        for(Role r : roles) {
            newUser.getRoles().add(new UserRoles(newUser, r));
        }
        var dataUser = userService.save(newUser);
        assertEquals(dataUser.getUsername(), newUser.getUsername().toLowerCase());
        assertNotNull(dataUser.getUserid());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ga_saveInvalidId() {
        User newUser = new User();
        newUser.setPassword("test");
        newUser.setUsername("test user");
        newUser.setPrimaryemail("test@user.com");
        newUser.setUserid(1000);
        List<Role> roles = roleService.findAll();
        for(Role r : roles) {
            newUser.getRoles().add(new UserRoles(newUser, r));
        }
        var dataUser = userService.save(newUser);
        assertEquals("", dataUser.getUsername());
    }

    @Test
    public void h_update() {
        var users = userService.findAll();
        var toUpdate = users.get(new Random().nextInt(users.size()));
        User u = new User();
        u.setPrimaryemail("test@test.com");
        u.setPassword("testpsw");
        u.setUsername("testuser");
        var updated = userService.update(u, toUpdate.getUserid());
        assertEquals(updated.getUsername(), u.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void ha_updateUserNotFound() {
        User newUser = new User();
        newUser.setPassword("test");
        newUser.setUsername("test user");
        newUser.setPrimaryemail("test@user.com");
        List<Role> roles = roleService.findAll();
        for(Role r : roles) {
            newUser.getRoles().add(new UserRoles(newUser, r));
        }
        var dataUser = userService.update(newUser, 1000);
    }

    @Test
    public void i_delete() {
        List<User> users = userService.findAll();
        var toDelete = users.get(new Random().nextInt(users.size()));
        userService.delete(toDelete.getUserid());
        assertEquals(users.size() - 1, userService.findAll().size());
    }

    @Test
    public void j_deleteAll() {
        userService.deleteAll();
        assertEquals(0, userService.findAll().size());
    }
}
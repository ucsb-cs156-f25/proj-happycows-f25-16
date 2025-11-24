package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.Course;
import edu.ucsb.cs156.happiercows.repositories.CourseRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Map;

import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CourseController.class)
public class CourseControllerTests extends ControllerTestCase {
    @MockBean
    CourseRepository courseRepository;

    @MockBean
    UserRepository userRepository;

    // Logged out users
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/course/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/course/1"))
                .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        Course course = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        mockMvc.perform(post("/api/course")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_delete() throws Exception {
        mockMvc.perform(delete("/api/course/1")
                .with(csrf()))
                .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_put() throws Exception {
        Course course = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        mockMvc.perform(put("/api/course/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().is(403));
    }

    // Regular users positive tests

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_courses() throws Exception {
        // arrange

        Course advapp = Course.builder()
            .code("CMPSC 156")
            .name("Advanced App Programming")
            .term("F24")
            .build();

        Course ethics = Course.builder()
            .code("ENGR")
            .name("Ethics in Engineering")
            .term("F24")
            .build();

        ArrayList<Course> expectedCourses = new ArrayList<>();
        expectedCourses.add(advapp);
        expectedCourses.add(ethics);

        when(courseRepository.findAll()).thenReturn(expectedCourses);

        // act
        MvcResult response = mockMvc.perform(get("/api/course/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(courseRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCourses);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_get_course_by_id() throws Exception {
        mockMvc.perform(get("/api/course/1"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_post_course() throws Exception {
        Course course = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        mockMvc.perform(post("/api/course")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_put_course() throws Exception {
        Course course = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        mockMvc.perform(put("/api/course/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_delete_course() throws Exception {
        mockMvc.perform(delete("/api/course/1")
                .with(csrf()))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_get_course_by_id() throws Exception {

        Course course = Course.builder()
            .id(7L)
            .code("CMPSC 156")
            .name("Advanced App Programming")
            .term("F24")
            .build();

        when(courseRepository.findById(7L)).thenReturn(Optional.of(course));

        MvcResult response = mockMvc.perform(get("/api/course/7"))
                .andExpect(status().isOk()).andReturn();

        verify(courseRepository, times(1)).findById(7L);
        String expectedJson = mapper.writeValueAsString(course);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_delete_course() throws Exception {

        Course course = Course.builder()
                .id(15L)
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        when(courseRepository.findById(15L)).thenReturn(Optional.of(course));

        MvcResult response = mockMvc.perform(delete("/api/course/15")
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(courseRepository, times(1)).findById(15L);
        verify(courseRepository, times(1)).deleteById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("course with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_delete_course_when_it_does_not_exist() throws Exception {
        when(courseRepository.findById(7L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/course/7")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(courseRepository, times(1)).findById(7L);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_get_course_when_it_does_not_exist() throws Exception {
        when(courseRepository.findById(7L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/course/7"))
                .andExpect(status().isNotFound());

        verify(courseRepository, times(1)).findById(7L);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_post_new_course() throws Exception {

        Course course = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        Course savedCourse = Course.builder()
                .id(123L)
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        when(courseRepository.save(course)).thenReturn(savedCourse);

        MvcResult response = mockMvc.perform(post("/api/course")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course)))
                .andExpect(status().isOk()).andReturn();

        verify(courseRepository, times(1)).save(course);
        String expectedJson = mapper.writeValueAsString(savedCourse);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_put_existing_course() throws Exception {

        Course originalCourse = Course.builder()
                .id(7L)
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("F24")
                .build();

        Course updatedCourse = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("W25")
                .build();

        when(courseRepository.findById(7L)).thenReturn(Optional.of(originalCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MvcResult response = mockMvc.perform(put("/api/course/7")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isOk()).andReturn();

        verify(courseRepository, times(1)).findById(7L);
        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository, times(1)).save(courseCaptor.capture());
        Course savedArgument = courseCaptor.getValue();
        assertEquals(7L, savedArgument.getId());
        assertEquals(updatedCourse.getCode(), savedArgument.getCode());
        assertEquals(updatedCourse.getName(), savedArgument.getName());
        assertEquals(updatedCourse.getTerm(), savedArgument.getTerm());
        String responseString = response.getResponse().getContentAsString();
        Course responseCourse = mapper.readValue(responseString, Course.class);
        assertEquals(savedArgument.getId(), responseCourse.getId());
        assertEquals(savedArgument.getCode(), responseCourse.getCode());
        assertEquals(savedArgument.getName(), responseCourse.getName());
        assertEquals(savedArgument.getTerm(), responseCourse.getTerm());
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_put_course_when_it_does_not_exist() throws Exception {
        Course updatedCourse = Course.builder()
                .code("CMPSC 156")
                .name("Advanced App Programming")
                .term("W25")
                .build();

        when(courseRepository.findById(7L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/course/7")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isNotFound());

        verify(courseRepository, times(1)).findById(7L);
    }

    @Test
    public void update_course_sets_all_fields() {
        Course existing = Course.builder()
                .id(9L)
                .code("OLD 9")
                .name("Old Name")
                .term("F20")
                .build();

        Course incoming = Course.builder()
                .code("NEW 9")
                .name("New Name")
                .term("W25")
                .build();

        when(courseRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseController controller = new CourseController();
        ReflectionTestUtils.setField(controller, "courseRepository", courseRepository);

        Course result = controller.updateCourse(9L, incoming);

        assertEquals(9L, result.getId());
        assertEquals(incoming.getCode(), result.getCode());
        assertEquals(incoming.getName(), result.getName());
        assertEquals(incoming.getTerm(), result.getTerm());
    }
}

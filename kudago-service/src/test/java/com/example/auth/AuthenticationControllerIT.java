package com.example.auth;

import com.example.BaseIT;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationControllerIT extends BaseIT {

  private static final String uri = "/api/v1/auth";

  @SneakyThrows
  @Test
  public void register_success() {
    var payload = RegistrationRequest.builder()
        .login("register-test")
        .displayName("Register Test")
        .password("password")
        .build();

    var response = register(payload);

    assertThat(response.token()).isNotEmpty();
  }

  @SneakyThrows
  @Test
  public void logout_success() {
    var payload = RegistrationRequest.builder()
        .login("logout-test")
        .displayName("Logout Test")
        .password("password")
        .build();

    var response = register(payload);

    assertThat(response.token()).isNotEmpty();

    mockMvc.perform(post(uri + "/logout")
            .header("Authorization", "Bearer %s".formatted(response.token())))
        .andExpectAll(
            status().isOk())
        .andReturn()
        .getResponse();
  }

  @SneakyThrows
  @Test
  public void changePassword_success() {
    var registerPayload = RegistrationRequest.builder()
        .login("change-password-test")
        .displayName("Change password Test")
        .password("password")
        .build();

    var response = register(registerPayload);

    assertThat(response.token()).isNotEmpty();

    var passwordChangePayload = ChangePasswordRequest.builder()
        .newPassword("password123")
        .twoFactorCode("0000")
        .build();

    mockMvc.perform(patch(uri + "/change-password")
            .header("Authorization", "Bearer %s".formatted(response.token()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordChangePayload)))
        .andExpectAll(
            status().isOk())
        .andReturn()
        .getResponse();

    var loginPayload = AuthenticationRequest.builder()
        .login(registerPayload.login())
        .password(passwordChangePayload.newPassword())
        .build();

    var loginResponse = mockMvc.perform(post(uri + "/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginPayload)))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var authResponse = objectMapper.readValue(loginResponse.getContentAsString(), AuthenticationResponse.class);

    assertThat(authResponse.token()).isNotEmpty();
  }

  @SneakyThrows
  public AuthenticationResponse register(RegistrationRequest request) {
    var mvcResponseRegister = mockMvc.perform(post(uri + "/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    return objectMapper.readValue(mvcResponseRegister.getContentAsString(), AuthenticationResponse.class);
  }
}

package com.yandex_backend_school.mega_market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 6:58 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class ImportsIntegrationTest {
  private final String baseUrl = "/imports";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private void makeRequestAndGetBadRequestStatusWithValidationFailedMessage(ImportsRequestBody requestBody)
    throws Exception {
    final MvcResult mvcResult = this.mockMvc.perform(post(baseUrl)
        .content(objectMapper.writeValueAsString(requestBody))
        .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn();

    final ErrorResponseBody responseBody = objectMapper.readValue(
      mvcResult.getResponse().getContentAsString(), ErrorResponseBody.class);

    assertNotNull(responseBody);
    assertNotNull(responseBody.getCode());
    assertEquals(400, responseBody.getCode().intValue());
    assertEquals(Message.VALIDATION_FAILED, responseBody.getMessage());
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseNameIsBlank() throws Exception {
    final ImportsRequestBodyItem requestBodyItem = new ImportsRequestBodyItem(
      UUID.randomUUID(),
      null,
      Type.CATEGORY,
      "",
      null);

    final ImportsRequestBody requestBody = new ImportsRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(new Date());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseCategoryIsNull() throws Exception {
    final ImportsRequestBodyItem requestBodyItem = new ImportsRequestBodyItem(
      UUID.randomUUID(),
      null,
      null,
      "category",
      null);

    final ImportsRequestBody requestBody = new ImportsRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(new Date());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseIdIsNull() throws Exception {
    final ImportsRequestBodyItem requestBodyItem = new ImportsRequestBodyItem(
      null,
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportsRequestBody requestBody = new ImportsRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(new Date());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseUpdateDateIsNull() throws Exception {
    final ImportsRequestBodyItem requestBodyItem = new ImportsRequestBodyItem(
      UUID.randomUUID(),
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportsRequestBody requestBody = new ImportsRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(null);

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnOkStatus() throws Exception {
    final ImportsRequestBodyItem requestBodyItem = new ImportsRequestBodyItem(
      UUID.randomUUID(),
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportsRequestBody requestBody = new ImportsRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(new Date());

    this.mockMvc.perform(post(baseUrl)
        .content(objectMapper.writeValueAsString(requestBody))
        .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());
  }
}

package com.yandex_backend_school.mega_market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import java.time.LocalDateTime;
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
public class ImportNodesIntegrationTest {
  private final String baseUrl = "/imports";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private void makeRequestAndGetBadRequestStatusWithValidationFailedMessage(ImportNodesRequestBody requestBody)
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
    final ImportNodesRequestBodyItem requestBodyItem = new ImportNodesRequestBodyItem(
      UUID.randomUUID().toString(),
      null,
      Type.CATEGORY,
      "",
      null);

    final ImportNodesRequestBody requestBody = new ImportNodesRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(LocalDateTime.now());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseCategoryIsNull() throws Exception {
    final ImportNodesRequestBodyItem requestBodyItem = new ImportNodesRequestBodyItem(
      UUID.randomUUID().toString(),
      null,
      null,
      "category",
      null);

    final ImportNodesRequestBody requestBody = new ImportNodesRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(LocalDateTime.now());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseIdIsNull() throws Exception {
    final ImportNodesRequestBodyItem requestBodyItem = new ImportNodesRequestBodyItem(
      null,
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportNodesRequestBody requestBody = new ImportNodesRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(LocalDateTime.now());

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnBadRequestStatusBecauseUpdateDateIsNull() throws Exception {
    final ImportNodesRequestBodyItem requestBodyItem = new ImportNodesRequestBodyItem(
      UUID.randomUUID().toString(),
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportNodesRequestBody requestBody = new ImportNodesRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(null);

    makeRequestAndGetBadRequestStatusWithValidationFailedMessage(requestBody);
  }

  @Test
  public void shouldReturnOkStatus() throws Exception {
    final ImportNodesRequestBodyItem requestBodyItem = new ImportNodesRequestBodyItem(
      UUID.randomUUID().toString(),
      null,
      Type.CATEGORY,
      "category",
      null);

    final ImportNodesRequestBody requestBody = new ImportNodesRequestBody();
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(LocalDateTime.now());

    this.mockMvc.perform(post(baseUrl)
        .content(objectMapper.writeValueAsString(requestBody))
        .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());
  }
}

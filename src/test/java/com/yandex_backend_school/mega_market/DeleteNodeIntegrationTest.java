package com.yandex_backend_school.mega_market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 11:13 AM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class DeleteNodeIntegrationTest {
  private final String baseUrl = "/delete";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldReturnBadRequestStatusBecauseIdIsInvalid() throws Exception {
    final MvcResult mvcResult = this.mockMvc.perform(
        delete(baseUrl + "/123"))
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
  public void shouldReturnItemNotFoundStatusBecauseNodeWithSpecifiedIdNotFound() throws Exception {
    final MvcResult mvcResult = this.mockMvc.perform(
        delete(baseUrl + "/863e1a7a-1304-42ae-943b-179184c077e3"))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andReturn();

    final ErrorResponseBody responseBody = objectMapper.readValue(
      mvcResult.getResponse().getContentAsString(), ErrorResponseBody.class);

    assertNotNull(responseBody);
    assertNotNull(responseBody.getCode());
    assertEquals(404, responseBody.getCode().intValue());
    assertEquals(Message.ITEM_NOT_FOUND, responseBody.getMessage());
  }

  @Test
  @Sql(value = {"/sql/insert_node.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/sql/truncate_node.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void shouldReturnOkStatus() throws Exception {
    mockMvc.perform(delete(
        baseUrl + "/863e1a7a-1304-42ae-943b-179184c077e3"))
      .andDo(print())
      .andExpect(status().isOk());
  }
}

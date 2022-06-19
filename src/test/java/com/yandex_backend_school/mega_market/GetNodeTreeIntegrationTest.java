package com.yandex_backend_school.mega_market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 19/06/2022 - 1:02 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class GetNodeTreeIntegrationTest {
  private final String baseUrl = "/nodes";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldReturnNotFoundStatusBecauseIdIsInvalid() throws Exception {
    final String id = "123";

    this.mockMvc.perform(get(baseUrl + "/" + id))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnItemNotFoundStatusBecauseNodeWithSpecifiedIdNotFound() throws Exception {
    final String id = "863e1a7a-1304-42ae-943b-179184c077e3";

    final MvcResult mvcResult = this.mockMvc.perform(get(baseUrl + "/" + id))
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
  @Sql(value = {"/sql/insert_offer.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/sql/truncate_node.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void shouldReturnOkStatusAndOffer() throws Exception {
    final String id = "863e1a7a-1304-42ae-943b-179184c077e3";

    final MvcResult mvcResult = this.mockMvc.perform(get(
        baseUrl + "/" + id))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn();

    final GetNodesResponseBody getNodesResponseBody = objectMapper.readValue(
      mvcResult.getResponse().getContentAsString(), GetNodesResponseBody.class);

    assertNotNull(getNodesResponseBody);
    assertEquals(getNodesResponseBody.getId(), id);
  }
}
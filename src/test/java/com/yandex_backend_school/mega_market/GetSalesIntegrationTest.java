package com.yandex_backend_school.mega_market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetSalesResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetSalesResponseBodyItem;
import java.util.List;
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
 * @created 20/06/2022 - 3:09 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class GetSalesIntegrationTest {
  private final String baseUrl = "/sales?date=";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldReturnBadRequestStatusBecauseDateNotMatchIsoFormat() throws Exception {
    final MvcResult mvcResult = this.mockMvc.perform(get(
        baseUrl + "2022-06-21 12:00:00.000Z"))
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
  @Sql(value = {"/sql/insert_nodes_with_date_difference.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/sql/truncate_node.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void shouldReturnOkStatusAndOneOfTwoNodes() throws Exception {
    final MvcResult mvcResult = this.mockMvc.perform(get(
        baseUrl + "2022-06-21T12:00:00.000Z"))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn();

    final GetSalesResponseBody responseBody = objectMapper.readValue(
      mvcResult.getResponse().getContentAsString(), GetSalesResponseBody.class);

    assertNotNull(responseBody);

    final List<GetSalesResponseBodyItem> items = responseBody.getItems();

    assertNotNull(items);
    assertEquals(1, items.size());

    final GetSalesResponseBodyItem item = items.get(0);

    assertNotNull(item);
    assertEquals(item.getName(), "first");
  }
}

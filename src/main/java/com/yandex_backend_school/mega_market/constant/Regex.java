package com.yandex_backend_school.mega_market.constant;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 1:09 PM
 */

public interface Regex {
  String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
  String POSITIVE_INTEGER_NUMBER = "^[0-9]*$";
}

package com.app.read.util;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DateUtils {

  public static long defaultEpochTime() {
    return Instant.now().getEpochSecond();
  }

}

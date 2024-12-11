package com.app.read.util;

import com.app.read.enums.OperationType;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class StatementUtils {

  public static String prepareQuery(String tableName, String schemaName, OperationType operationType,
      Map<String, Object> headerRecord) {
    if (Objects.nonNull(schemaName)) {
      tableName = schemaName + "." + tableName;
    }
    StringBuilder builder = new StringBuilder();
    // currently only inserts are supported
    if (OperationType.INSERT.equals(operationType)) {
      builder.append("INSERT INTO ");
      builder.append(tableName);
      builder.append(headerRecord.keySet().stream().collect(Collectors.joining(", ", " ( ", " ) ")).toUpperCase());
      builder.append(" VALUES ");
      builder.append(headerRecord.keySet().stream().map(val -> ":" + val).collect(Collectors.joining(", ", " ( ", " ) ")));
      return builder.toString();
    }

    throw new UnsupportedOperationException("Operation not supported");
  }


}
package com.app.read.util;

import com.app.read.enums.OperationType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class StatementUtils {

  public static String prepareQuery(String tableName, String schemaName, OperationType operationType,
      Map<String, Object> headerRecord, List<String> updateColumns) {
    if (Objects.nonNull(schemaName)) {
      tableName = schemaName + "." + tableName;
    }
    StringBuilder builder = new StringBuilder();
    if (OperationType.INSERT.equals(operationType)) {
      builder.append("INSERT INTO ");
      builder.append(tableName);
      builder.append(headerRecord.keySet().stream().collect(Collectors.joining(", ", " ( ", " ) ")).toUpperCase());
      builder.append(" VALUES ");
      builder.append(headerRecord.keySet().stream().map(val -> ":" + val).collect(Collectors.joining(", ", " ( ", " ) ")));
      return builder.toString();
    } else if (OperationType.UPDATE.equals(operationType)) {
      builder.append("UPDATE ");
      builder.append(tableName);
      builder.append(" SET ");
      builder.append(headerRecord.keySet().stream()
          .filter(Predicate.not(key -> updateColumns.stream().anyMatch(col -> col.equalsIgnoreCase(key))))
          .map(val -> String.join("= :", val, val)).collect(Collectors.joining(", ")));
      builder.append(" WHERE ");
      builder.append(headerRecord.keySet().stream()
          .filter(key -> updateColumns.stream().anyMatch(col -> col.equalsIgnoreCase(key)))
          .map(val -> String.join("= :", val, val)).collect(Collectors.joining(" AND ")));
      return builder.toString();
    } else if (OperationType.DELETE.equals(operationType)) {
      builder.append("DELETE FROM ");
      builder.append(tableName);
      builder.append(" WHERE ");
      builder.append(headerRecord.keySet().stream()
          .filter(key -> updateColumns.stream().anyMatch(col -> col.equalsIgnoreCase(key)))
          .map(val -> String.join("= :", val, val)).collect(Collectors.joining(" AND ")));
      return builder.toString();
    }

    throw new UnsupportedOperationException("Operation not supported");
  }


}

package com.app.read.model;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Table {

  private String name;
  private String schema;
  private List<Column> columns;

  public Map<String, Column> getColumnMap() {
    return columns.stream().collect(Collectors.toMap(Column::getMapping, Function.identity()));
  }
}
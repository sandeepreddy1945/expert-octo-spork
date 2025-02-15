package com.app.read.model;

import com.app.read.enums.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {

  private String name;
  private String type;
  private String mapping;
  private GenerationType generationType;
  /**
   * Default value is not required for #GenerationType.DEFAULT_LONG_TIMESTAMP
   */
  private String defaultValue;
}
package com.app.read.util;

import com.app.read.model.Column;
import com.app.read.model.Metadata;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DataUtils {


  public static List<Map<String, Object>> cleanUpRecords(List<Map<String, Object>> records, Metadata metadata) {
    Map<String, Column> columnMap = metadata.getTable().getColumnMap();
    List<Map<String, Object>> resultMap = new ArrayList<>();
    for (Map<String, Object> record : records) {
      Map<String, Object> resultRecord = new LinkedHashMap<>();
      for (Entry<String, Object> entry : record.entrySet()) {
        Column column = columnMap.get(entry.getKey());
        if (Objects.nonNull(column)) {
          resultRecord.put(column.getName(), convertedValue(entry.getValue(), column));
        }
      }
      resultMap.add(resultRecord);
    }

    return resultMap;
  }

  public static Object convertedValue(Object value, Column column) {
    if (Objects.isNull(value)) {
      return null;
    }
    if (column.getType().equalsIgnoreCase("int")) {
      if (value instanceof BigDecimal decimal) {
        return decimal.intValue();
      }
      return Integer.parseInt(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("float")) {
      if (value instanceof BigDecimal decimal) {
        return decimal.floatValue();
      }
      return Float.parseFloat(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("double")) {
      if (value instanceof BigDecimal decimal) {
        return decimal.doubleValue();
      }
      return Double.parseDouble(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("string")) {
      return String.valueOf(value);
    } else if (column.getType().equalsIgnoreCase("number")) {
      if (value instanceof BigDecimal decimal) {
        return decimal;
      }
      return new BigDecimal(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("date")) {
      return java.sql.Date.valueOf(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("local-date")) {
      return LocalDate.parse(String.valueOf(value));
    } else if (column.getType().equalsIgnoreCase("timestamp")) {
      return Timestamp.valueOf(String.valueOf(value));
    }

    return value;
  }

  public static Object convertedValue(Cell cell) {
    if (Objects.nonNull(cell)) {
      if (cell.getType().equals(CellType.NUMBER)) {
        return cell.asNumber().setScale(2, RoundingMode.HALF_UP);
      } else if (cell.getType().equals(CellType.BOOLEAN)) {
        return cell.asBoolean();
      } else if (cell.getType().equals(CellType.STRING)) {
        return cell.asString();
      } else if (cell.getType().equals(CellType.EMPTY)) {
        return null;
      } else if (CellType.FORMULA.equals(cell.getType())) {
        return cell.getRawValue();
      } else {
        return cell.getRawValue();
      }
    }
    return null;
  }

  public static Object convertedCellValue(Cell cell, Column columnMetadata) {
    Object convertedValue = convertedValue(cell);
    return convertedValue(convertedValue, columnMetadata);
  }
}

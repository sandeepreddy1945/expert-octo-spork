package com.app.read.util;

import com.app.read.model.Column;
import com.app.read.model.Metadata;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XLUtils {

  private static final int HEADER_ROW = 1;

  public static List<Map<String, Object>> readFile(String filePath) throws IOException {
    try (FileInputStream inputStream = FileUtils.openInputStream(new File(filePath))) {
      return readFileInputStream(inputStream);
    }
  }

  public static List<Map<String, Object>> readFile(String filePath, Metadata metadata) throws IOException {
    try (FileInputStream inputStream = FileUtils.openInputStream(new File(filePath))) {
      return readFileInputStream(inputStream, metadata);
    }
  }

  public static List<Map<String, Object>> readFile(InputStream in, Metadata metadata) throws IOException {
    return readFileInputStream(in, metadata);
  }


  public static List<Map<String, Object>> readFileInputStream(InputStream in) throws IOException {
    return readFileInputStream(in, null);
  }

  public static List<Map<String, Object>> readFileInputStream(InputStream in, Metadata metadata) throws IOException {
    try (ReadableWorkbook wb = new ReadableWorkbook(in)) {
      // always read only the first sheet in the workbook
      Sheet sheet = wb.getFirstSheet();
      Map<Integer, String> headers = new LinkedHashMap<>();
      List<Map<String, Object>> records = new ArrayList<>();
      Map<String, Column> columnMap = null;
      if (Objects.nonNull(metadata)) {
        columnMap = metadata.getTable().getColumnMap();
      }
      try (Stream<Row> rowStream = sheet.openStream()) {
        List<Row> rows = rowStream.toList();
        for (Row row : rows) {
          if (row.getRowNum() == HEADER_ROW) {
            List<Cell> cells = row.stream().toList();
            for (Cell cell : cells) {
              int column = cell.getAddress().getColumn();
              String header = cell.asString();
              headers.put(column, header);
            }
          } else {
            List<Cell> cells = row.stream().toList();
            Map<String, Object> record = new LinkedHashMap<>();
            for (Cell cell : cells) {
              int column = cell.getAddress().getColumn();
              String header = headers.get(column);
              if (Objects.nonNull(columnMap)) {
                Column columnMetadata = columnMap.get(header);
                if (Objects.nonNull(columnMetadata)) {
                  header = columnMetadata.getName();
                  record.put(header, DataUtils.convertedCellValue(cell, columnMetadata));
                }
              } else {
                record.put(header, DataUtils.convertedValue(cell));
              }
            }
            records.add(record);
          }
        }
      }
      return records;
    }
  }

}

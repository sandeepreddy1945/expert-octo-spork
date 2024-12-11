package com.app.read.util;

import com.app.read.enums.OperationType;
import com.app.read.model.Metadata;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVUtils {

  public static List<Map<String, Object>> readFile(String filePath) throws IOException {
    try (FileInputStream inputStream = FileUtils.openInputStream(new File(filePath))) {
      return readFile(inputStream);
    }
  }

  public static List<Map<String, Object>> readFile(String filePath, Metadata metadata) throws IOException {
    try (FileInputStream inputStream = FileUtils.openInputStream(new File(filePath))) {
      return readFile(inputStream, metadata);
    }
  }

  public static List<Map<String, Object>> readFile(InputStream inputStream) throws IOException {
    return readFile(inputStream, null);
  }

  public static List<Map<String, Object>> readFile(InputStream inputStream, Metadata metadata) throws IOException {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.setDateFormat(new StdDateFormat());
    CsvSchema schema = CsvSchema.emptySchema().withHeader();
    csvMapper.registerModule(new JavaTimeModule());
    MappingIterator<Map<String, Object>> mappingIterator = csvMapper.readerFor(Map.class).with(schema).readValues(inputStream);
    List<Map<String, Object>> records = mappingIterator.readAll();
    log.info("Total number of records from csv file is: {}", records.size());
    if (Objects.nonNull(metadata)) {
      // if metadata is available, clean up the records using metadata
      return DataUtils.cleanUpRecords(records, metadata);
    }

    return records;

  }

  public static void main(String[] args) throws IOException {
    List<Map<String, Object>> records = readFile("src/main/resources/static/SampleData.csv");
    Map<String, Object> headerRecord = records.get(0);
    String query = StatementUtils.prepareQuery("order", null, OperationType.INSERT, headerRecord);
    System.out.println(query);
  }

}

package com.app.read.service.impl;

import static com.app.read.util.DateUtils.defaultEpochTime;

import com.app.read.enums.FileType;
import com.app.read.enums.GenerationType;
import com.app.read.enums.OperationType;
import com.app.read.model.Column;
import com.app.read.model.Metadata;
import com.app.read.repository.StatementExecutionRepository;
import com.app.read.service.StatementExecutionService;
import com.app.read.util.CSVUtils;
import com.app.read.util.DataUtils;
import com.app.read.util.StatementUtils;
import com.app.read.util.XLUtils;
import com.app.read.util.YamlUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementExecutionServiceImpl implements StatementExecutionService {

  private final StatementExecutionRepository executionRepository;


  @Override
  public void executeStatement(String tableName, OperationType operationType, String schemaName, FileType fileType,
      String fileLocalPath, boolean isMetadataProvided, List<String> updateWhereColumns, List<String> defaultTimestampColumns)
      throws IOException {

    List<Map<String, Object>> records = List.of();
    List<Column> defaultGeneratedColumns = List.of();
    if (FileType.CSV.equals(fileType)) {
      if (Objects.isNull(fileLocalPath)) {
        fileLocalPath = "src/main/resources/static/SampleData.csv";
      }
      if (isMetadataProvided) {
        String metadataLocation = String.join("/", StringUtils.substringBeforeLast(fileLocalPath, "/"), "metadata.yml");
        Metadata metadata = YamlUtils.readMetadata(metadataLocation);
        defaultGeneratedColumns = metadata.getTable().getDefaultColumns();
        ;
        records = CSVUtils.readFile(fileLocalPath, metadata);
      } else {
        records = CSVUtils.readFile(fileLocalPath);
      }

    } else if (FileType.XL.equals(fileType)) {
      if (Objects.isNull(fileLocalPath)) {
        fileLocalPath = "src/main/resources/static/SampleData.xlsx";
      }
      if (isMetadataProvided) {
        String metadataLocation = String.join("/", StringUtils.substringBeforeLast(fileLocalPath, "/"), "metadata.yml");
        Metadata metadata = YamlUtils.readMetadata(metadataLocation);
        defaultGeneratedColumns = metadata.getTable().getDefaultColumns();
        records = XLUtils.readFile(fileLocalPath, metadata);
      } else {
        records = XLUtils.readFile(fileLocalPath);
      }
    }

    updateDefaultTimestampColumns(defaultTimestampColumns, records);

    // check on default generated columns
    updateMetadataGeneratedFields(defaultGeneratedColumns, records);

    if (CollectionUtils.isNotEmpty(records)) {
      String query = StatementUtils.prepareQuery(tableName, schemaName, operationType, records.get(0), updateWhereColumns);
      log.info(query);
      executionRepository.executeStatements(query, records.toArray(Map[]::new));
    }
  }


  @Override
  public void executeStatementFromFiles(String tableName, OperationType operationType, String schemaName, FileType fileType,
      boolean isMetadataProvided, List<String> updateWhereColumns, MultipartFile dataFile, MultipartFile metadataFile,
      List<String> defaultTimestampColumns)
      throws IOException {

    List<Map<String, Object>> records = List.of();
    List<Column> defaultGeneratedColumns = List.of();
    fileType = determineFileTye(fileType, dataFile);
    if (FileType.CSV.equals(fileType)) {
      if (isMetadataProvided && metadataFile != null) {
        Metadata metadata = YamlUtils.readMetadata(metadataFile.getInputStream());
        defaultGeneratedColumns = metadata.getTable().getDefaultColumns();
        records = CSVUtils.readFile(dataFile.getInputStream(), metadata);
      } else {
        records = CSVUtils.readFile(dataFile.getInputStream());
      }

    } else if (FileType.XL.equals(fileType)) {
      if (isMetadataProvided && metadataFile != null) {
        Metadata metadata = YamlUtils.readMetadata(metadataFile.getInputStream());
        defaultGeneratedColumns = metadata.getTable().getDefaultColumns();
        records = XLUtils.readFile(dataFile.getInputStream(), metadata);
      } else {
        records = XLUtils.readFileInputStream(dataFile.getInputStream());
      }
    }

    updateDefaultTimestampColumns(defaultTimestampColumns, records);

    // check on default generated columns
    updateMetadataGeneratedFields(defaultGeneratedColumns, records);

    if (CollectionUtils.isNotEmpty(records)) {
      String query = StatementUtils.prepareQuery(tableName, schemaName, operationType, records.get(0), updateWhereColumns);
      log.info(query);
      executionRepository.executeStatements(query, records.toArray(Map[]::new));
    }
  }

  private FileType determineFileTye(FileType fileType, MultipartFile file) {
    if (Objects.nonNull(fileType)) {
      return fileType;
    } else {
      String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
      if (StringUtils.equalsIgnoreCase(extension, "csv")) {
        return FileType.CSV;
      } else {
        return FileType.XL;
      }
    }
  }

  private static void updateMetadataGeneratedFields(List<Column> defaultGeneratedColumns, List<Map<String, Object>> records) {
    if (CollectionUtils.isNotEmpty(defaultGeneratedColumns)) {
      for (Map<String, Object> record : records) {
        defaultGeneratedColumns.forEach(column -> {
          boolean isKeyPresent = record.containsKey(column.getName());
          if (column.getGenerationType().equals(GenerationType.DEFAULT_LONG_TIMESTAMP)) {
            if (!isKeyPresent) {
              record.put(column.getName(), defaultEpochTime());
            } else if (Objects.isNull(record.get(column.getMapping()))) {
              record.put(column.getName(), defaultEpochTime());
            }
          } else if (column.getGenerationType().equals(GenerationType.VALUE)) {
            if (!isKeyPresent) {
              record.put(column.getName(), DataUtils.convertedValue(column.getDefaultValue(), column));
            } else if (Objects.isNull(record.get(column.getMapping()))) {
              record.put(column.getName(), DataUtils.convertedValue(column.getDefaultValue(), column));
            }
          }
        });
      }
    }
  }

  private static void updateDefaultTimestampColumns(List<String> defaultTimestampColumns, List<Map<String, Object>> records) {
    if (CollectionUtils.isNotEmpty(defaultTimestampColumns)) {
      // update the record map, ensure the defaultTimestampColumns are of the same names as the column names in file provided.
      records.forEach(map -> defaultTimestampColumns.forEach(column -> {
        boolean isKeyPresent = map.containsKey(column);
        if (!isKeyPresent) {
          map.put(column, defaultEpochTime());
        } else if (Objects.isNull(map.get(column))) {
          map.put(column, defaultEpochTime());
        }
      }));
    }
  }

}

package com.app.read.service.impl;

import com.app.read.enums.FileType;
import com.app.read.enums.OperationType;
import com.app.read.model.Metadata;
import com.app.read.repository.StatementExecutionRepository;
import com.app.read.service.StatementExecutionService;
import com.app.read.util.CSVUtils;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementExecutionServiceImpl implements StatementExecutionService {

  private final StatementExecutionRepository executionRepository;


  @Override
  public void executeStatement(String tableName, OperationType operationType, String schemaName, FileType fileType,
      String fileLocalPath, boolean isMetadataProvided, List<String> updateWhereColumns) throws IOException {

    List<Map<String, Object>> records = List.of();
    if (FileType.CSV.equals(fileType)) {
      if (Objects.isNull(fileLocalPath)) {
        fileLocalPath = "src/main/resources/static/SampleData.csv";
      }
      if (isMetadataProvided) {
        String metadataLocation = String.join("/", StringUtils.substringBeforeLast(fileLocalPath, "/"), "metadata.yml");
        Metadata metadata = YamlUtils.readMetadata(metadataLocation);
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
        records = XLUtils.readFile(fileLocalPath, metadata);
      } else {
        records = XLUtils.readFile(fileLocalPath);
      }
    }

    if (CollectionUtils.isNotEmpty(records)) {
      String query = StatementUtils.prepareQuery(tableName, schemaName, operationType, records.get(0), updateWhereColumns);
      log.info(query);
      executionRepository.executeStatements(query, records.toArray(Map[]::new));
    }
  }


}

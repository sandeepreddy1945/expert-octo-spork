package com.app.read.rest;

import com.app.read.enums.FileType;
import com.app.read.enums.OperationType;
import com.app.read.service.StatementExecutionService;

import java.io.IOException;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UtilityController {

  private final StatementExecutionService statementExecutionService;

  @PostMapping(path = {"/api/v1/file-execute", "/api/v2/file-execute", "/v3/file-execute"})
  public ResponseEntity<String> executeFileByPath(@RequestParam(name = "tableName") String tableName,
      @RequestParam(name = "operationType", required = false, defaultValue = "INSERT") OperationType operationType,
      @RequestParam(name = "schemaName", required = false) String schemaName,
      @RequestParam(name = "fileType", required = false, defaultValue = "XL") FileType fileType,
      @RequestParam(name = "filePath", required = false) String fileLocalPath,
      @RequestParam(name = "metadataProvided", required = false) boolean isMetadataProvided,
      @RequestParam(name = "updateWhereColumns", required = false, defaultValue = "") List<String> updateWhereColumns)
      throws IOException {
    if (OperationType.UPDATE.equals(operationType) && CollectionUtils.isEmpty(updateWhereColumns)) {
      throw new IllegalArgumentException("updateWhereColumns cannot be empty");
    }
    statementExecutionService.executeStatement(tableName, operationType, schemaName, fileType, fileLocalPath, isMetadataProvided,
        updateWhereColumns);

    return ResponseEntity.ok("SUCCESS");
  }

  @PostMapping(path = {"/api/v1/upload-execute", "/api/v2/upload-execute", "/v3/upload-execute"})
  public ResponseEntity<String> executeMultiPartFiles(@RequestParam(name = "tableName") String tableName,
      @RequestParam(name = "operationType", required = false, defaultValue = "INSERT") OperationType operationType,
      @RequestParam(name = "schemaName", required = false) String schemaName,
      @RequestParam(name = "fileType", required = false, defaultValue = "XL") FileType fileType,
      @RequestParam(name = "metadataProvided", required = false) boolean isMetadataProvided,
      @RequestParam(name = "updateWhereColumns", required = false, defaultValue = "") List<String> updateWhereColumns,
      @RequestPart(name = "dataFile") MultipartFile dataFile,
      @RequestParam(name = "metadata", required = false) MultipartFile metadata)
      throws IOException {
    if (OperationType.UPDATE.equals(operationType) && CollectionUtils.isEmpty(updateWhereColumns)) {
      throw new IllegalArgumentException("updateWhereColumns cannot be empty");
    }
    statementExecutionService.executeStatementFromFiles(tableName, operationType, schemaName, fileType, isMetadataProvided,
        updateWhereColumns, dataFile, metadata);

    return ResponseEntity.ok("SUCCESS");
  }
}

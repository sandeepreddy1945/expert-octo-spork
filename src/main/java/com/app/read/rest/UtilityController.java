package com.app.read.rest;

import com.app.read.enums.FileType;
import com.app.read.enums.OperationType;
import com.app.read.service.StatementExecutionService;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestParam(name = "metadataProvided", required = false) boolean isMetadataProvided) throws IOException {
    statementExecutionService.executeStatement(tableName, operationType, schemaName, fileType, fileLocalPath, isMetadataProvided);

    return ResponseEntity.ok("SUCCESS");
  }
}

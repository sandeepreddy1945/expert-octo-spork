package com.app.read.service;

import com.app.read.enums.FileType;
import com.app.read.enums.OperationType;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface StatementExecutionService {

  void executeStatement(String tableName, OperationType operationType, String schemaName,
      FileType fileType, String fileLocalPath, boolean isMetadataProvided, List<String> updateWhereColumns) throws IOException;

  void executeStatementFromFiles(String tableName, OperationType operationType, String schemaName, FileType fileType,
      boolean isMetadataProvided, List<String> updateWhereColumns, MultipartFile dataFile, MultipartFile metadata)
      throws IOException;
}

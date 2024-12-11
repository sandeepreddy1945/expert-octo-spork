package com.app.read.service;

import com.app.read.enums.FileType;
import com.app.read.enums.OperationType;
import java.io.IOException;
import java.util.List;

public interface StatementExecutionService {

  void executeStatement(String tableName, OperationType operationType, String schemaName,
      FileType fileType, String fileLocalPath, boolean isMetadataProvided, List<String> updateWhereColumns) throws IOException;
}

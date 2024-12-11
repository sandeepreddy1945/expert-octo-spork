package com.app.read.util;

import com.app.read.model.Metadata;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YamlUtils {

  public static Metadata readMetadata(String filePath) throws IOException {
    YAMLMapper yamlMapper = new YAMLMapper();
    try (InputStream inputStream = FileUtils.openInputStream(new File(filePath))) {
      return yamlMapper.readValue(inputStream, Metadata.class);
    }
  }
}

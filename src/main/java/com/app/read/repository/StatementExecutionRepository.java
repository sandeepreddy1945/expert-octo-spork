package com.app.read.repository;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StatementExecutionRepository {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Transactional
  public int[] executeStatements(String sql, Map<String, Object>[] params) {
    return this.namedParameterJdbcTemplate.batchUpdate(sql, params);
  }
}

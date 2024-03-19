package com.jiawa.wiki.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/excelToSql")
public class ExcelToSQLController {
  @PostMapping("/fetch")
  public void excelToSql(MultipartFile file) {
    try {
      String sql = excel2Sql2(file);
      System.out.println(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String excel2Sql2(MultipartFile file) {

    try {
      StringBuilder sqlBuilder = new StringBuilder();

      InputStream inputStream = file.getInputStream();

      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

      Sheet sheet = workbook.getSheetAt(0);

      Row headerRow = sheet.getRow(0);

      String tableName = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0];

      String columns = getHeader(headerRow);

      Iterator<Row> iterator = sheet.iterator();

      while (iterator.hasNext()) {

        Row currentRow = iterator.next();

        if (currentRow.getRowNum() == 0) {
          continue;
        }

        String values = getRowData(currentRow);

        String sql = String.format("REPLACE INTO %s (%s) VALUES (%s);",
            tableName, columns, values);

        sqlBuilder.append(sql).append("\n");
      }
      return sqlBuilder.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getHeader(Row headerRow) {

    StringJoiner columnNames = new StringJoiner(",");

    for (Cell cell : headerRow) {
      columnNames.add(cell.getStringCellValue());
    }

    return columnNames.toString();

  }

  private static String getRowData(Row row) {
    StringJoiner values = new StringJoiner(",");

    for (Cell cell : row) {
      switch (cell.getCellType()) {
        case STRING:
          if (org.springframework.util.StringUtils.hasText(cell.getStringCellValue())) {
            values.add("'" + cell.getStringCellValue().replace("\"", "\\\"").replace("'", "\\'") + "'"); // 转义单引号
          } else {
            values.add("NULL");
          }
          break;
        case NUMERIC:
          values.add(String.valueOf((int) cell.getNumericCellValue()));
          break;
        case BOOLEAN:
          values.add(cell.getBooleanCellValue() ? "1" : "0");
          break;
        case FORMULA:
          values.add(cell.getCellFormula());
          break;
        default:
          values.add("NULL");
      }
    }

    return values.toString();
  }

}

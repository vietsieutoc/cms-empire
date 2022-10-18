package com.yuri.empire.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CreateHeroPoolUtil {

    public static void initKingDom001() {
        long size = 100_000;




    }

    private static void init(long size, double[] jobRate, int[] jobType, double[] rarityRate, int[] rarityType) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("data");
        for (int i = 0; i < 1; i++) {
            Row row = sheet.createRow(i);
            List<String> files = Arrays.asList("id", "mId", "poolId");
            for (int j = 0; j < files.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(files.get(j));
            }
        }
        exportFile("template/SHeroPool.xlsx", workbook);
    }

    private static void exportFile(String fileName, Workbook workbook) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

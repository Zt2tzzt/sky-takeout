package com.sky;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class POITest {
    /**
     * 此方法用于：创建 Excel 文件，并写入内容。
     */
    private static void write() throws IOException {
        // 在内存中，创建一个工作簿
        XSSFWorkbook xlsx = new XSSFWorkbook();
        // 在工作簿中，创建一个工作表
        XSSFSheet sheet1 = xlsx.createSheet("Sheet1");

        // 在工作表中，创建一行（rownum 编号从 0 开始）
        XSSFRow row = sheet1.createRow(1);

        // 在行中，创建单元格（cellnum 编号从 0 开始），并写入内容。
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("城市");

        // 再创建一行
        row = sheet1.createRow(2);
        row.createCell(1).setCellValue("张三");
        row.createCell(2).setCellValue("北京");

        // 再创建一行
        row = sheet1.createRow(3);
        row.createCell(1).setCellValue("李四");
        row.createCell(2).setCellValue("上海");

        FileOutputStream fos = new FileOutputStream("info.xlsx");
        xlsx.write(fos);
        fos.close();
        xlsx.close();
    }

    /**
     * 此方法用于：读取 Excel 文件。
     */
    private static void read() throws IOException {
        // 读取磁盘上已经存在的 Excel 文件。
        FileInputStream fis = new FileInputStream("info.xlsx");
        XSSFWorkbook xlsx = new XSSFWorkbook(fis);
        // 读取第一个工作表
        XSSFSheet sheet1 = xlsx.getSheetAt(0);

        // 获取工作表最后一行的行号（行号从 0 开始）
        int lastRowNum = sheet1.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            // 获取某一行
            XSSFRow row = sheet1.getRow(i);
            String cellVal1 = row.getCell(1).getStringCellValue();
            String cellVal2 = row.getCell(2).getStringCellValue();
            System.out.println(cellVal1 + " " + cellVal2);
        }

        fis.close();
        xlsx.close();
    }

    public static void main(String[] args) throws IOException {
        write();
        read();
    }
}

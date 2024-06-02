package com.maestrx.studentcontrol.teacherapp.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelStyles(workbook: XSSFWorkbook) {

    private val numberDataFormat = workbook.createDataFormat().getFormat("0")
    private val percentFormat = workbook.createDataFormat().getFormat("0%")

    private val baseFont: XSSFFont = workbook.createFont().apply {
        fontName = "Times New Roman"
        fontHeightInPoints = 12
    }

    private val boldFont: XSSFFont = workbook.createFont().apply {
        fontName = "Times New Roman"
        fontHeightInPoints = 12
        bold = true
    }

    val textCenter: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val textLeft: CellStyle =
        workbook.createCellStyle().apply {
            setFont(baseFont)
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val decimal: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            dataFormat = numberDataFormat
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val percent: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            dataFormat = percentFormat
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val headerText: CellStyle =
        workbook.createCellStyle().apply {
            wrapText = true
            setAlignment(HorizontalAlignment.CENTER)
            verticalAlignment = VerticalAlignment.CENTER
            setFont(boldFont)
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val headerDecimal: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            verticalAlignment = VerticalAlignment.CENTER
            setFont(boldFont)
            dataFormat = numberDataFormat
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val headerPercent: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(boldFont)
            dataFormat = percentFormat
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

    val number: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }
}
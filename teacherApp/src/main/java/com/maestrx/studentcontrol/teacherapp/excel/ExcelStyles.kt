package com.maestrx.studentcontrol.teacherapp.excel

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelStyles(workbook: XSSFWorkbook) {

    private val numberDataFormat = workbook.createDataFormat().getFormat("0")
    private val percentFormat = workbook.createDataFormat().getFormat("0%")
    private val dateFormat = workbook.createDataFormat().getFormat("dd.MM")

    private val baseFont: XSSFFont = workbook.createFont().apply {
        fontName = "Times New Roman"
        fontHeightInPoints = 12
    }

    val textCenter: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
        }

    val textLeft: CellStyle =
        workbook.createCellStyle().apply {
            setFont(baseFont)
        }

    var number: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            dataFormat = numberDataFormat
        }

    val percent: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            dataFormat = percentFormat
        }

    val date: CellStyle =
        workbook.createCellStyle().apply {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(baseFont)
            dataFormat = dateFormat
        }
}
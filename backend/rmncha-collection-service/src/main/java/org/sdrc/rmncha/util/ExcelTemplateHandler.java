package org.sdrc.rmncha.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTemplateHandler {

	public static void main(String[] args) {
		
		String[] excludedFields = "data.f1q_district!data.f1FacilityType,data.f1FacilityLevel#data.f1q_district,data.f1FacilityType!data.f1FacilityLevel#data.f1q_district,data.f1FacilityLevel!data.f1FacilityType#data.f1q_district,data.f1FacilityType,data.f1FacilityLevel"
				.split("#");
		String str = "data.f1q_district,data.f1FacilityType,data.f1FacilityLevel!data.f1FacilityLevel".split("!")[0].replaceAll("data.", "");
		
		String remainder = str.substring(str.indexOf(",")+1, str.length());
		
		System.out.println(remainder);
//		
//		String Str = new String("Welcome-to-Tutorialspoint.com");
//	      System.out.println("Return Value :" );
//	      
//	      for (String retval: Str.split("-", 2)) {
//	         System.out.println(retval);
//	      }
	}
	
	
//	public static void main(String[] args) {
		
		

		/*String path = "F:\\DATA OLD\\Sarita\\The Workspace\\SI-RMNCH+A\\sirmncha-engine_r1_code.xlsx";

		XSSFWorkbook workbook = null;

		try {
			workbook = new XSSFWorkbook(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet questionSheet = workbook.getSheetAt(0);
		for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
			// loop
			if (questionSheet.getRow(row) == null)
				break;
			XSSFRow xssfRow = questionSheet.getRow(row);
			Cell cell = xssfRow.getCell(7);
			
			try {
				if (cell != null && CellType.STRING == cell.getCellTypeEnum()
						&& cell.getStringCellValue().equals("dropdown-score")) {
//					copyRow(workbook, questionSheet, row, row+1);
					  // Get the source / new row
					
					System.out.println("row number-->>"+row);
					
					//unomment this in 1st time run to insert row
//			        XSSFRow newRow = questionSheet.getRow(row+1);
			        // If the row exist in destination, push down all rows by 1 else create a new row
//			        if (newRow != null) {
//			        	questionSheet.shiftRows(row+1, questionSheet.getLastRowNum(), 1);
//			        } else {
//			            newRow = questionSheet.createRow(row+1);
//			        }
					
					
					//uncomment this while populating score-holder
			        XSSFRow newRow = questionSheet.createRow(row+1);
			        XSSFRow sourceRow = questionSheet.getRow(row);

			        sourceRow.createCell(14).setCellValue("score:"+sourceRow.getCell(6));
			        // Loop through source columns to add to new row
			        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			            // Grab a copy of the old/new cell
			            Cell oldCell = sourceRow.getCell(i);
			            Cell newCell = newRow.createCell(i);

			            // If the old cell is null jump to next cell
			            if (oldCell == null) {
			                newCell = null;
			                continue;
			            }

			            // Copy style from old cell and apply to new cell
			            XSSFCellStyle newCellStyle = workbook.createCellStyle();
			            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			            
			            newCell.setCellStyle(newCellStyle);

			            // If there is a cell comment, copy
			            if (oldCell.getCellComment() != null) {
			                newCell.setCellComment(oldCell.getCellComment());
			            }

			            // If there is a cell hyperlink, copy
			            if (oldCell.getHyperlink() != null) {
			                newCell.setHyperlink(oldCell.getHyperlink());
			            }

			            // Set the cell data type
			            newCell.setCellType(oldCell.getCellTypeEnum());

			            // Set the cell data value
						switch (oldCell.getCellTypeEnum()) {
						case BLANK:
							newCell.setCellValue(oldCell.getStringCellValue());
							break;
						case BOOLEAN:
							newCell.setCellValue(oldCell.getBooleanCellValue());
							break;
						case ERROR:
							newCell.setCellErrorValue(oldCell.getErrorCellValue());
							break;
						case FORMULA:
							newCell.setCellFormula(oldCell.getCellFormula());
							break;
						case NUMERIC:
							newCell.setCellValue(oldCell.getNumericCellValue());
							break;
						case STRING:
				        	switch (i) {
		                	
		                	case 5 : //in question cell append it with score
		                		newCell.setCellValue("Score ("+ oldCell.getRichStringCellValue()+")");
		                		break;
		                	case 6 : //in column_name cell append it with score
		                		newCell.setCellValue("score"+ oldCell.getRichStringCellValue());
		                		break;
		                	case 7: 
		                		newCell.setCellValue("score-holder");
		                		break;
		                	case 8: 
		                		newCell.setCellValue("tel");
		                		break;
		                	case 9:
		                	case 10:
		                	case 11:
		                	case 12:
		                	case 13:
		                		newCell.setCellValue("");
		                		break;
//		                	case 14:
//		                		newCell.setCellValue("score:"+sourceRow.getCell(6));
//		                		break;
		                	default:
		                			 newCell.setCellValue(oldCell.getRichStringCellValue());
		                   
		                    break;
		                	}
							break;
						default:
							newCell.setCellValue(oldCell.getRichStringCellValue());

						}
//						++row;
			        }

					
				}
			}catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( "F:\\DATA OLD\\Sarita\\The Workspace\\SI-RMNCH+A\\sirmncha-engine_r1_code_scoreholder.xlsx");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			workbook.write(fos);
			workbook.close();
			System.out.println("done");
			if(fos!=null)
				fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		

//	}
	
	 private static XSSFWorkbook copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {
	        // Get the source / new row
	        XSSFRow newRow = worksheet.getRow(destinationRowNum);
	        XSSFRow sourceRow = worksheet.getRow(sourceRowNum);

	        // If the row exist in destination, push down all rows by 1 else create a new row
	        if (newRow != null) {
	            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
	        } else {
	            newRow = worksheet.createRow(destinationRowNum);
	        }

	        // Loop through source columns to add to new row
	        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
	            // Grab a copy of the old/new cell
	            Cell oldCell = sourceRow.getCell(i);
	            Cell newCell = newRow.createCell(i);

	            // If the old cell is null jump to next cell
	            if (oldCell == null) {
	                newCell = null;
	                continue;
	            }

	            // Copy style from old cell and apply to new cell
	            XSSFCellStyle newCellStyle = workbook.createCellStyle();
	            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
	            
	            newCell.setCellStyle(newCellStyle);

	            // If there is a cell comment, copy
	            if (oldCell.getCellComment() != null) {
	                newCell.setCellComment(oldCell.getCellComment());
	            }

	            // If there is a cell hyperlink, copy
	            if (oldCell.getHyperlink() != null) {
	                newCell.setHyperlink(oldCell.getHyperlink());
	            }

	            // Set the cell data type
	            newCell.setCellType(oldCell.getCellTypeEnum());

	            // Set the cell data value
	            switch (oldCell.getCellTypeEnum()) {
	                case BLANK:
	                    newCell.setCellValue(oldCell.getStringCellValue());
	                    break;
	                case BOOLEAN:
	                    newCell.setCellValue(oldCell.getBooleanCellValue());
	                    break;
	                case ERROR:
	                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
	                    break;
	                case FORMULA:
	                    newCell.setCellFormula(oldCell.getCellFormula());
	                    break;
	                case NUMERIC:
	                    newCell.setCellValue(oldCell.getNumericCellValue());
	                    break;
	                case STRING:
	                	newCell.setCellValue(oldCell.getRichStringCellValue());
	                	/*switch (i) {
	                	
	                	case 5 : //in question cell append it with score
	                		newCell.setCellValue("Score ("+ oldCell.getRichStringCellValue()+")");
	                		break;
	                	case 6 : //in column_name cell append it with score
	                		newCell.setCellValue("score"+ oldCell.getRichStringCellValue());
	                		break;
	                	case 7: 
	                		newCell.setCellValue("score-holder");
	                		break;
	                	case 8: 
	                		newCell.setCellValue("tel");
	                		break;
	                	case 9:
	                	case 10:
	                	case 11:
	                	case 12:
	                	case 13:
	                		newCell.setCellValue("");
	                		break;
	                	case 14:
	                		newCell.setCellValue("score:"+sourceRow.getCell(6));
	                		break;
	                	default:
	                			 newCell.setCellValue(oldCell.getRichStringCellValue());
	                   
	                    break;
	                	}*/
	              
	            }
	        }

	        // If there are are any merged regions in the source row, copy to new row
	        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
	            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
	            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
	                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
	                        (newRow.getRowNum() +
	                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
	                                        )),
	                        cellRangeAddress.getFirstColumn(),
	                        cellRangeAddress.getLastColumn());
	                worksheet.addMergedRegion(newCellRangeAddress);
	            }
	        }
	        
	        return workbook;
	    }
}

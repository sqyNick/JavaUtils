package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	//默认单元格内容为数字时格式
	private static DecimalFormat df = new DecimalFormat("0");
	// 默认单元格格式化日期字符串 
	private static SimpleDateFormat sdf = new SimpleDateFormat(  "yyyy-MM-dd HH:mm:ss"); 
	// 格式化数字
	private static DecimalFormat nf = new DecimalFormat("0.0");  
	public static ArrayList<ArrayList<ArrayList<Object>>> readExcel(File file){
		if(file == null){
			return null;
		}
		if(file.getName().endsWith("xlsx")){
			//处理ecxel2007
			return readExcel2007(file);
		}else{
			//处理ecxel2003
			return readExcel2003(file);
		}
	}
	/*
	 * @return 将返回结果存储在ArrayList内，存储结构与二位数组类似
	 * lists.get(0).get(0).get(0)表示过去Excel中第一张表0行0列单元格
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> readExcel2003(File file){
		try{
			ArrayList<ArrayList<ArrayList<Object>>> sheetArray = new ArrayList<ArrayList<ArrayList<Object>>> ();
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			for(int sheetNum = 0;sheetNum < wb.getNumberOfSheets();sheetNum++){
				ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
				ArrayList<Object> colList;
				HSSFSheet sheet = wb.getSheetAt(sheetNum);
				HSSFRow row;
				HSSFCell cell;
				Object value;
				for(int i = 0 , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
					row = sheet.getRow(i);
					colList = new ArrayList<Object>();
					if(row == null){
						//当读取行为空时
						if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
							rowList.add(colList);
						}
						continue;
					}else{
						rowCount++;
					}
					for( int j = 0 ; j <= row.getLastCellNum() ;j++){
						cell = row.getCell(j);
						if(cell == null ){
							//当该单元格为空
							if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
								colList.add("");
							}
							continue;
						}
						switch(cell.getCellType()){
						 case XSSFCell.CELL_TYPE_STRING:  
			                    value = cell.getStringCellValue();  
			                    break;  
			                case XSSFCell.CELL_TYPE_NUMERIC:  
			                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {  
			                        value = df.format(cell.getNumericCellValue());  
			                    } else if ("General".equals(cell.getCellStyle()  
			                            .getDataFormatString())) {  
			                        value = nf.format(cell.getNumericCellValue());  
			                    } else {  
			                        value = sdf.format(HSSFDateUtil.getJavaDate(cell  
			                                .getNumericCellValue()));  
			                    }  
			                    break;  
			                case XSSFCell.CELL_TYPE_BOOLEAN:  
			                    value = Boolean.valueOf(cell.getBooleanCellValue());
			                    break;  
			                case XSSFCell.CELL_TYPE_BLANK:  
			                    value = "";  
			                    break;  
			                default:  
			                    value = cell.toString();  
						}// end switch
						colList.add(value);
					}//end for j
					rowList.add(colList);
				}//end for i
				sheetArray.add(rowList);
			}// end sheetNum
			
			return sheetArray;
		}catch(Exception e){
			return null;
		}
	}
	
	public static ArrayList<ArrayList<ArrayList<Object>>>  readExcel2007(File file){
		try{
			ArrayList<ArrayList<ArrayList<Object>>> sheetArray = new ArrayList<ArrayList<ArrayList<Object>>> ();
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
			for(int sheetNum = 0;sheetNum < wb.getNumberOfSheets();sheetNum++){
				ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
				ArrayList<Object> colList;
				XSSFSheet sheet = wb.getSheetAt(sheetNum);
				XSSFRow row;
				XSSFCell cell;
				Object value;
				for(int i = 0 , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
					row = sheet.getRow(i);
					colList = new ArrayList<Object>();
					if(row == null){
						//当读取行为空时
						if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
							rowList.add(colList);
						}
						continue;
					}else{
						rowCount++;
					}
					for( int j = 0 ; j <= row.getLastCellNum() ;j++){
						cell = row.getCell(j);
						if(cell == null ){
							//当该单元格为空
							if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
								colList.add("");
							}
							continue;
						}
						switch(cell.getCellType()){
						 case XSSFCell.CELL_TYPE_STRING:  
			                    value = cell.getStringCellValue();  
			                    break;  
			                case XSSFCell.CELL_TYPE_NUMERIC:  
			                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {  
			                        value = df.format(cell.getNumericCellValue());  
			                    } else if ("General".equals(cell.getCellStyle()  
			                            .getDataFormatString())) {  
			                        value = nf.format(cell.getNumericCellValue());  
			                    } else {  
			                        value = sdf.format(HSSFDateUtil.getJavaDate(cell  
			                                .getNumericCellValue()));  
			                    }  
			                    break;  
			                case XSSFCell.CELL_TYPE_BOOLEAN:  
			                    value = Boolean.valueOf(cell.getBooleanCellValue());
			                    break;  
			                case XSSFCell.CELL_TYPE_BLANK:  
			                    value = "";  
			                    break;  
			                default:  
			                    value = cell.toString();  
						}// end switch
						colList.add(value);
					}//end for j
					rowList.add(colList);
				}//end for i
				sheetArray.add(rowList);
			}// end sheetNum
			return sheetArray;
		}catch(Exception e){
			return null;
		}
	}
	/*
	 * 根据获取的result值，创建基本的Excel
	 */
	public static void writeExcel(ArrayList<ArrayList<ArrayList<Object>>> result,String path){
		if(result == null){
			return;
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		for(int sheetNum = 0;sheetNum < result.size(); sheetNum++){
			HSSFSheet sheet = wb.createSheet("sheet"+sheetNum);
			for(int i = 0 ;i < result.get(sheetNum).size() ; i++){
				 HSSFRow row = sheet.createRow(i);
				if(result.get(sheetNum).get(i) != null){
					for(int j = 0; j < result.get(sheetNum).get(i).size() ; j ++){
						HSSFCell cell = row.createCell(j);
						cell.setCellValue(result.get(sheetNum).get(i).get(j).toString());
					}
				}
			}
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            wb.write(os);
        } catch (IOException e){
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File(path);//Excel文件生成后存储的位置。
        OutputStream fos  = null;
        try
        {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }           
	}
	
	public static DecimalFormat getDf() {
		return df;
	}
	public static void setDf(DecimalFormat df) {
		ExcelUtil.df = df;
	}
	public static SimpleDateFormat getSdf() {
		return sdf;
	}
	public static void setSdf(SimpleDateFormat sdf) {
		ExcelUtil.sdf = sdf;
	}
	public static DecimalFormat getNf() {
		return nf;
	}
	public static void setNf(DecimalFormat nf) {
		ExcelUtil.nf = nf;
	}
	
	
	
}

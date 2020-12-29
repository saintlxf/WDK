package pers.lxf.wdk.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {
    public static String getCellString(Cell cell){
        if(cell==null||cell.getCellType()!= CellType.STRING){
            return null;
        }
        return cell.getStringCellValue();
    }
    public static int getCellInt(Cell cell){
        if(cell==null||cell.getCellType()!=CellType.NUMERIC){
            return -1;
        }
        return (int)cell.getNumericCellValue();
    }
}

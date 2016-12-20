/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.driver;

import gov.inl.LIVE.common.IDriver;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.ApplicationContext;
import java.io.File;
import java.io.IOException;
import gov.inl.LIVE.entity.WaterData;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.lang.Math;

/**
 *
 * @author SZEWTG
 */
public class WaterDriver  implements IDriver {
    
    private static int stopRow = 0;
    private static int previousRow = 0;
    private static int stopColumn = 0;
    private static int pipeRow = 4;
    private static double initTime = 0.0;
    private static double previousTime = 0.0;
    private static double dataDelta = 0.0;
    private static double timeDelta = 0.0;
    private Workbook wb;
    private Sheet sheetStream;
    private Sheet sheetPipe;
    private double endTime;
    
    
    private static final int START_COLUMN_HEADER = 1;
    private static final int START_ROW_HEADER = 1;
    private static final int START_ROW_DATA = 4;
    private static final int COLUMN_OFFSET = 6;
    private static final int TEMP_COLUMN = 3;
    private static final int PRESS_COLUMN = 4;
    private static final int TIME_COLUMN = 1;
    private static final int MAX_ENTRIES = 1000;
    int count = 0;
    
    @Override
    public void init(ApplicationContext context)
    {
        /*
        this.nbodyInfoDAO = context.getBean(NbodyInfoDAO.class);
        this.nbodyDAO = context.getBean(NbodyDAO.class);
        
        this.nbodyInfo = nbodyInfoDAO.findById(nbodyInfoId);
        
        triple = nbodyDAO.getCriteriaTriple();
        cb = triple.getCriteriaBuilder();
        root = triple.getRoot();
        orderBy[0] = cb.asc(root.get("step"));
        orderBy[1] = cb.asc(root.get("planetNumber"));
        */
        
                    // open the file
            // TODO: this should probably be a pop up and more advance checking of file could be implemented

                    System.out.println("opening file");
        try {
            wb = WorkbookFactory.create(new File("C:/Users/SZEWTG/Downloads/water pipe dynamic result 2016 9 13.xlsx"));

        } catch (IOException ex) {
            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
            sheetStream = wb.getSheetAt(0);
            sheetPipe = wb.getSheetAt(1);
            endTime = sheetStream.getRow(sheetStream.getLastRowNum()).getCell(TIME_COLUMN).getNumericCellValue();
    }

    // startTime - the time the data should start at
    // timestep - how much to skip of the data/how fast should the play back be
    // resolution - ???
    // maxResults - ???
    @Override
    public List getData(double startTime, double timestep, double resolution, long maxResults)
    {
        System.out.println("RUNNING PARSER");      

        if (initTime == 0.0)
        {
            initTime = System.currentTimeMillis();
        }
        
        List<WaterData> list = new ArrayList<>();
        try {
            if (resolution == 0.0)
            {
                resolution = 1.0;
            }
            
            //get the row corresponding to the given start time
            if (startTime != 0.0)
            {
                stopRow = (int) Math.floor((startTime) * 100.0);
            }
            
            //always send the first data set, then check timings of things
            if (stopRow != 0)
            {
                double currentRowTime = sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue();
                double previousRowTime = sheetStream.getRow(START_ROW_DATA + stopRow - 1).getCell(TIME_COLUMN).getNumericCellValue();
                System.out.println("current row " + currentRowTime
                        + " previous row " + previousRowTime
                        + " speed " + resolution);
                
                double timeScale = 3600000; //hours to milliseconds
                dataDelta = ((currentRowTime - previousRowTime)/1.0) * timeScale;

                timeDelta = (System.currentTimeMillis() - previousTime);
                
                System.out.println("dataDelta " + dataDelta + " timeDelta " + timeDelta);

                // if we haven't reached the time to send the next set data, just leave
                if (dataDelta > timeDelta)
                {
                    return list;
                }

                double skip;

                double scheduleRate = 0.1;

                previousRow = stopRow;

                // if the data is required to be sent faster than the schedule rate, figure out how much data to skip
                if (dataDelta < scheduleRate && dataDelta > 0.0)
                {
                    skip = scheduleRate / dataDelta;

                    stopRow = (int) Math.floor((skip + 
                            sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue() * 100.0));
                    
                    // if we try to go past the data set, wrap around to the appropriate spot
                    if (stopRow + START_ROW_DATA > sheetStream.getPhysicalNumberOfRows())
                    {
                        stopRow = stopRow - (sheetStream.getPhysicalNumberOfRows() - START_ROW_DATA);
                    }
                }
            }
            WaterData waterData = new WaterData();
                                                                        
            list = new ArrayList<>(sheetStream.getPhysicalNumberOfRows() * sheetStream.getRow(1).getPhysicalNumberOfCells());
            

            
            //if (System.currentTimeMillis() > sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue()*1000/resolution + initTime)
            //while((stopRow < (sheetStream.getPhysicalNumberOfRows() - START_ROW_DATA)) && (count < MAX_ENTRIES))
            {
                System.out.println("row " + stopRow);
                                
                // if we need to start from first column again
                if (stopColumn == 5)
                {
                    stopColumn = 0;    
                }
                
                // add in pipe data first
                if (stopColumn == 0){
                    double delta = (System.currentTimeMillis() - initTime)/1000;
                    System.out.println("C " + delta + " S " + sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue() + " P " + sheetPipe.getRow(pipeRow).getCell(1).getNumericCellValue());
                    
        
                    // calcuate the offset of the when pipe data starts in context of stream data
                    double pipeOffset = sheetPipe.getRow(4).getCell(1).getNumericCellValue();
                    
                    //if the current row time is greater than the start time of pipe data
                    if (sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue() >= pipeOffset)
                    {
                        pipeRow = (int)(2.0*(sheetStream.getRow(START_ROW_DATA +  stopRow).getCell(TIME_COLUMN).getNumericCellValue() - pipeOffset)*100.0);
                        String cellContent = sheetPipe.getRow(0).getCell(1).getStringCellValue();
                        waterData.setType("Pipe");
                        String id = "";
                        
                        if (cellContent != null && cellContent.length() > 0)
                        {
                            // get which pipe this is
                            for (int cc = 0; Character.isDigit(cellContent.charAt(cc)); cc++)
                            {
                                id += cellContent.charAt(cc);
                            }

                            if (!"".equals(id))
                            {
                                // set the pipe id now
                                waterData.setId(Long.parseLong(id));
                            }
                            
                            // set the temperatures along the pipe
                            for(int kk = 0; kk < 11; kk++)
                            {
                                waterData.setTemp(sheetPipe.getRow(pipeRow + 1).getCell(2 + kk).getNumericCellValue(), kk);
                            }

                            waterData.setTime(sheetPipe.getRow(pipeRow).getCell(1).getNumericCellValue());
                            list.add(waterData);
                            count++;
                                                    System.out.println("Temp P" + Arrays.toString(waterData.getTemp()));
                            // skip over every other row 
                            pipeRow = pipeRow + 2;
                        }
                    }
                }
                
                while ((stopColumn < sheetStream.getRow(1).getPhysicalNumberOfCells()) && (count < MAX_ENTRIES))
                {
                    String cellContent = sheetStream.getRow(START_ROW_HEADER).getCell(START_COLUMN_HEADER + COLUMN_OFFSET * stopColumn).getStringCellValue();
                    int clen = cellContent.length();

                    // if the pseudo header cell has an something in it with an s then we found a stream set
                    if (clen > 1 && cellContent != null && cellContent.contains("s")){
                        waterData.setType("Stream");
                        waterData.setId(Long.parseLong(cellContent.substring(1,clen)));
                        //System.out.println("TIME S" + sheetStream.getRow(START_ROW_DATA + stopRow).getCell(TIME_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue());
                        waterData.setTemp(sheetStream.getRow(START_ROW_DATA + stopRow).getCell(TEMP_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue(), 0);
                        waterData.setTime(sheetStream.getRow(START_ROW_DATA + stopRow).getCell(TIME_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue());
                        list.add(waterData);
                        
                        count++;

                    }
                    stopColumn++;
                }
                stopRow++;
            }

            // if we reached max entries to send this time, save off where to beging for next time
            if (count >= MAX_ENTRIES){
                System.out.println("Reached Max entries to send");
            }
            
            // else we have reached the end of the file, reset the starting positions to start from beginning
            if (count <= MAX_ENTRIES && (stopColumn < sheetStream.getRow(1).getPhysicalNumberOfCells()) && (count < MAX_ENTRIES) && (stopColumn < sheetStream.getRow(1).getPhysicalNumberOfCells()))
            {
                System.out.println("Reached End of file restarting");
                stopRow = 0;
                stopColumn = 0;
                pipeRow = 4;
            }


            System.out.println("SIZE " + count + "Stop Row" + stopRow + "Stop Column" + stopColumn);
        } catch (Throwable e) {
             System.out.println(e.getMessage());
        }

        
        previousTime = System.currentTimeMillis();
        return list;
    }

    @Override
    public double getStartTime()
    {
        return 0.0;
    }
    
    @Override
    public double getEndTime()
    {
        return endTime;
    }
    
    @Override
    public void shutdown()
    {
        
    }
    
    
}

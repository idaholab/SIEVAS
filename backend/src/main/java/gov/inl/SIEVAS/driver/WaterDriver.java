/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.driver;

import gov.inl.SIEVAS.common.DriverOption;
import gov.inl.SIEVAS.common.IDriver;
import gov.inl.SIEVAS.controller.DriverController;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.ApplicationContext;
import java.io.File;
import java.io.IOException;
import gov.inl.SIEVAS.entity.WaterData;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Scanner;

/**
 * Class to get water security testbed data.
 * @author SZEWTG
 */
public class WaterDriver  implements IDriver {
    
    private static int stopColumn = 0;
    private static double scheduledTime = 0.0;
    private static double dataDelta = 0.0;
    private static int dataIndex = 0;
    private Workbook wb;
    private Sheet[] sheetStream;
    private Sheet[] sheetPipe;
    private double endTime;
    private int streamCount = 0;
    private int pipeCount = 0;
    
    private int numberOfSheets;
    
    private static final int START_COLUMN_HEADER = 1;
    private static final int START_ROW_HEADER = 1;
    private static final int START_ROW_DATA = 4;
    private static final int COLUMN_OFFSET = 6;
    private static final int TEMP_COLUMN = 3;
    private static final int TIME_COLUMN = 1;
    private static final String FILENAME_OPTION = "filename";
    int count = 0;
    
    @Override
    public List<DriverOption> getOptionList()
    {
        DriverOption option = new DriverOption(FILENAME_OPTION,"");
        List<DriverOption> list = new ArrayList<>();
        list.add(option);
        return list;
    }
    
    @Override
    public void init(ApplicationContext context, List<DriverOption> options)
    {
        // open the file
        // TODO: this should probably be a pop up and more advance checking of file could be implemented

        //System.out.println(System.getProperty("line.separator")+System.getProperty("line.separator")+"Please enter the path for the excel file that has water data:");
        //Scanner keyboard = new Scanner(System.in);
        //String filename = keyboard.nextLine();
        String filename = "";
        for(DriverOption option: options)
        {
            if (option.getOptionName().toLowerCase().equals(FILENAME_OPTION))
                filename = option.getOptionValue();
        }
        
        Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "opening file " + filename + System.getProperty("line.separator")+System.getProperty("line.separator"));
        try
        {
            wb = WorkbookFactory.create(new File(filename));

        }
        catch (IOException ex)
        {
            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        catch (InvalidFormatException ex)
        {
            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        numberOfSheets = wb.getNumberOfSheets();
        sheetStream = new Sheet[numberOfSheets];
        sheetPipe = new Sheet[numberOfSheets];

        for (int i = 0; i < numberOfSheets; i++)
        {
            Cell cellContentStream = null;
            Cell cellContentPipe = null;

            if (wb.getSheetAt(i).getRow(1) != null)
            {
                cellContentStream = wb.getSheetAt(i).getRow(1).getCell(1);
            }

            if (wb.getSheetAt(i).getRow(0) != null)
            {
                cellContentPipe = wb.getSheetAt(i).getRow(0).getCell(1);
            }


            // if the pseudo header cell has an something in it with an s then we found a stream sheet
            if (cellContentStream != null && (cellContentStream.getStringCellValue().contains("s") || cellContentStream.getStringCellValue().contains("S"))){
                sheetStream[i-pipeCount] = wb.getSheetAt(i);
                streamCount++;
            }

            // if the pseudo header cell has an something in it with pipe or Pipe then we found a pipe sheet
            if (cellContentPipe != null && (cellContentPipe.getStringCellValue().contains("pipe") || cellContentPipe.getStringCellValue().contains("Pipe"))){
                sheetPipe[i-streamCount] = wb.getSheetAt(i);
                pipeCount++;
            }

        }

        if(streamCount > 0)
        {
            endTime = sheetStream[0].getRow(sheetStream[0].getLastRowNum()).getCell(TIME_COLUMN).getNumericCellValue();
        }
    }

    // startTime - the time the data should start at
    // timestep - how much to skip of the data/how fast should the play back be
    // resolution - ???
    // maxResults - ???
    @Override
    public List getData(double startTime, double timestep, double resolution, long maxResults)
    {
        Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "RUNNING PARSER");
        
        double currentSystemTime = System.currentTimeMillis();
        List<WaterData> list = new ArrayList<>();
        
        // if we have stream data continue on, other wise don't do anything
        if (streamCount != 0);
        {
            // If we have an updated start time, offset the data to account for the new start time
            if (startTime >= 0.0d)
            {
                setDataIndex(getIndexAtTime(startTime));

                // set to run this data set immediately
                setScheduledTime(0.0);
                Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Setting Start Time " + startTime);
            }

            double delta = currentSystemTime - getScheduledTime();
            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "System Time - Scheduled time " +  delta);

            // if we have reached the time for the next data to go out
            if (currentSystemTime >= getScheduledTime() )
            {
                Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Current Index " + getDataIndex());

                // process pipe data
                list = setPipeData(getDataIndex(), list);

                //process stream data
                list = setStreamData(getDataIndex(), list);

                //update the index for next run through 
                setDataIndex(getNextDataIndex(timestep));

                Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Next Index " + getDataIndex());

            }
        }      
        
        return list;
        
    }
    
    // get the current index that should be process
    private int getDataIndex()
    {
        return dataIndex;
    }
    
    // set the index that should be processed
    private void setDataIndex(int index)
    {
        dataIndex = index;
    }
    
    // given a time (with reference to data time), get the corresponding index
    private int getIndexAtTime(double time)
    {
        return (int)time*100;
    }
    
    // given an index, get the time for that index
    private double getTimeAtIndex(int index)
    {
        return sheetStream[0].getRow(START_ROW_DATA +  index).getCell(TIME_COLUMN).getNumericCellValue();
    }
    
    // get the time the next data should go out
    private double getScheduledTime()
    {
        return scheduledTime;
    }
    
    // set the next time data should go out
    private void setScheduledTime(double time)
    {
        scheduledTime = time;
    }
    
    private int getNextDataIndex(double speed)
    {
            int currentIndex = getDataIndex();
            int previousIndex = currentIndex - 1;

            if (currentIndex == 0)
            {
                return currentIndex + 1;
            }
            
            double currentRowTime = sheetStream[0].getRow(START_ROW_DATA +  currentIndex).getCell(TIME_COLUMN).getNumericCellValue();
            double previousRowTime = sheetStream[0].getRow(START_ROW_DATA + previousIndex).getCell(TIME_COLUMN).getNumericCellValue();

            double timeScale = 1;//3600000; hours to milliseconds
            dataDelta = ((currentRowTime - previousRowTime)/speed) * timeScale;

            Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "dataDelta " + dataDelta);

            double skip;
            int nextIndex;

            // very important!!! if scheduled rate changes in DVRService, this needs to be updated as well
            // TODO: make method to get AMQ schedule rate
            double scheduleRate = 0.1;

            // if the data is required to be sent faster than the schedule rate, figure out how much data to skip
            if (dataDelta < scheduleRate && dataDelta > 0.0)
            {
                skip = scheduleRate / dataDelta;

                nextIndex = (int) Math.floor((skip + getDataIndex()));               
                setScheduledTime(0.0);
            }
            else
            {
                nextIndex = getDataIndex() + 1;
                setScheduledTime(dataDelta + System.currentTimeMillis());
            }
                
            // if we try to go past the data set, wrap around to the appropriate spot
            if (nextIndex + START_ROW_DATA >= sheetStream[0].getLastRowNum())
            {
                nextIndex = nextIndex - (sheetStream[0].getPhysicalNumberOfRows() - START_ROW_DATA);
            }
            
        return nextIndex;
    }
    
    
    // set the pipe data at the given index in the provided list
    List<WaterData> setPipeData(int index, List<WaterData> list)
    {
        // go through each sheet
        for (int sheetNum = 0; sheetNum < pipeCount; sheetNum++)
        {
            int numberOfPipes = sheetPipe[sheetNum].getRow(0).getPhysicalNumberOfCells();

            // go through each pipe in sheet
            for (int pipeNum = 0; pipeNum < numberOfPipes; pipeNum++)
            {
                int offset = pipeNum * 13; 

                double pipeStartTime = sheetPipe[sheetNum].getRow(4).getCell(1+offset).getNumericCellValue();
                WaterData waterData = new WaterData();

                //if the current row time for stream data is greater than  or equal to the start time of pipe data, start adding in pipe data
                if (getTimeAtIndex(index) >= pipeStartTime)
                {
                    //ugh precision problems....
                    int t1 =(int)(getTimeAtIndex(index) * 100);
                    int t2 = (int)(pipeStartTime * 100);
                    int pipeIndex = 2*(t1-t2)+5;

                    if (pipeIndex >= sheetPipe[sheetNum].getLastRowNum())
                    {
                        continue;
                    }
                    
                    String cellContent = sheetPipe[sheetNum].getRow(0).getCell(1+offset).getStringCellValue();

                    String id = "";

                    if (cellContent != null && cellContent.length() > 0)
                    {
                        // get which pipe this is
                        for (int cc = 4; cc < cellContent.length(); cc++)
                        {
                            if (Character.isDigit(cellContent.charAt(cc)))
                            {
                                id += cellContent.charAt(cc);
                            }
                        }

                        if (!"".equals(id))
                        {
                            // set the pipe id now
                            waterData.setId(Long.parseLong(id));
                        }

                        waterData.setType("Pipe" + id);
                        Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, " pipeindex " + pipeIndex);
                        // set the temperatures along the pipe
                        for(int kk = 0; kk < 11; kk++)
                        {
                            waterData.setTemp(sheetPipe[sheetNum].getRow(pipeIndex).getCell(2 + kk + offset).getNumericCellValue(), kk);
                        }

                        waterData.setTime(getTimeAtIndex(index));

                        list.add(waterData);
                        int row = pipeIndex;
                        Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Pipe Data @ Row " + row + "     " + waterData);
                    }
                }
                else
                {

                }
            }
        }
        return list;
    }
    
    // set the stream data at the given index in the provided list
    List<WaterData> setStreamData(int index, List<WaterData> list)
    {        
        Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Setting Stream Data");
        // process stream data
        while ((stopColumn < sheetStream[0].getRow(START_ROW_HEADER).getPhysicalNumberOfCells()))
        {
            String cellContent = sheetStream[0].getRow(START_ROW_HEADER).getCell(START_COLUMN_HEADER + COLUMN_OFFSET * stopColumn).getStringCellValue();
            int clen = cellContent.length();

            // if the pseudo header cell has an something in it with an s then we found a stream set
            if (clen > 0 && (cellContent.contains("s") || cellContent.contains("S"))){
                // get new water data
                WaterData waterData = new WaterData();

                waterData.setType("Stream" + cellContent);
                waterData.setId(Long.parseLong(cellContent.substring(1,clen)));
                //System.out.println("TIME S" + sheetStream.getRow(START_ROW_DATA + stopRow).getCell(TIME_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue());
                waterData.setTemp(sheetStream[0].getRow(START_ROW_DATA + index).getCell(TEMP_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue(), 0);
                waterData.setTime(sheetStream[0].getRow(START_ROW_DATA + index).getCell(TIME_COLUMN + COLUMN_OFFSET * stopColumn).getNumericCellValue());
                list.add(waterData);
                int row = START_ROW_DATA + index;
                Logger.getLogger(WaterDriver.class.getName()).log(Level.SEVERE, "Stream Data @ Row " + row + "     " + waterData);

            }
            stopColumn++;
        }

        if (stopColumn >= sheetStream[0].getRow(1).getPhysicalNumberOfCells())
        {
            stopColumn = 0;
        }

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

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: May 31, 2017
 */


CREATE OR REPLACE FUNCTION POPULATE_DATASOURCE() RETURNS VOID AS $$
DECLARE
    datasourceId bigint;
BEGIN
    INSERT INTO datasource(name,description,driver_name) VALUES('NBody Datasource', 'NBody Datasource','gov.inl.SIEVAS.driver.NbodyDriver');
    datasourceId:=LASTVAL();
    

    INSERT INTO datasource(name,description,driver_name) VALUES('UAV Datasource', 'UAV Datasource','gov.inl.SIEVAS.driver.UavDriver');
    datasourceId:=LASTVAL();
    INSERT INTO datasource_option(datasource_id, option_name,option_value) VALUES(datasourceId,'path','IRC_UAV_Images/');

    INSERT INTO datasource(name,description,driver_name) VALUES('Water Security Datasource', 'Water Security Testbed Datasource','gov.inl.SIEVAS.driver.WaterDriver');
    datasourceId:=LASTVAL();
    INSERT INTO datasource_option(datasource_id, option_name,option_value) VALUES(datasourceId,'filename','all pipes results and valve opening 9 27 2016.xlsx');

END
$$
LANGUAGE plpgsql VOLATILE;


SELECT POPULATE_DATASOURCE();

DROP FUNCTION POPULATE_DATASOURCE();


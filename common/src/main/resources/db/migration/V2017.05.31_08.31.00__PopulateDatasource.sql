/* 
 * Copyright 2017 Idaho National Laboratory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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


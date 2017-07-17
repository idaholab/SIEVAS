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
/**
 * Author:  monejh
 * Created: Aug 22, 2016
 */

CREATE OR REPLACE FUNCTION POPULATE_NBODY() RETURNS VOID AS $$
DECLARE
    nbodyId bigint;
BEGIN
    INSERT INTO nbody_info(timestep) VALUES(6.103515625E-5);
    nbodyId:=LASTVAL();

    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,1,'Sun',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,2,'Mecury',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,3,'Venus',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,4,'Earth',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,5,'Mars',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,6,'Jupiter',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,7,'Saturn',NULL); 
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,8,'Uranus',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,9,'Neptune',NULL);
    INSERT INTO nbody_planet(nbody_info_id,planet_num,planet_name,planet_texture) VALUES(nbodyId,10,'Pluto',NULL);
END
$$
LANGUAGE plpgsql VOLATILE;


SELECT POPULATE_NBODY();

DROP FUNCTION POPULATE_NBODY();
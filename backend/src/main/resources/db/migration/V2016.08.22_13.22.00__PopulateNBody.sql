/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Aug 18, 2016
 */

CREATE TABLE nbody_info
(
    id bigserial,
    timestep double precision,
    primary key(id)
);

CREATE TABLE nbody_planet
(
    id bigserial,
    nbody_info_id bigint REFERENCES nbody_info(id),
    planet_num int NOT NULL,
    planet_name varchar(128) NOT NULL,
    planet_texture bytea NULL,
    primary key(id)
);


CREATE TABLE nbody
(
    id bigserial,
    planet_num int NOT NULL,
    step bigint NOT NULL,
    x double precision NOT NULL,
    y double precision NOT NULL,
    z double precision NOT NULL,
    u double precision NOT NULL,
    v double precision NOT NULL,
    w double precision NOT NULL,
    primary key(id)
);


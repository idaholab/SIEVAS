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


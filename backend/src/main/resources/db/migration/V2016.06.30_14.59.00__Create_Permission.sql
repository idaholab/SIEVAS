/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Jun 30, 2016
 */

CREATE TABLE permission
(
    id bigserial,
    permission_name varchar(128) NOT NULL UNIQUE,
    description text NULL,
    primary key(id)
);


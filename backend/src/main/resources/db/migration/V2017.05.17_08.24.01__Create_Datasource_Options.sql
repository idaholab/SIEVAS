/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: May 17, 2017
 */

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Jun 30, 2016
 */

CREATE TABLE datasource_option
(
    id bigserial,
    datasource_id bigint NOT NULL REFERENCES datasource(id),
    option_name varchar(128) NOT NULL,
    option_value varchar(1024) NOT NULL,
    UNIQUE (datasource_id,option_name),
    primary key(id)
);


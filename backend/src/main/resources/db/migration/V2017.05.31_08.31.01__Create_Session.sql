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

CREATE TABLE session
(
    id bigserial,
    name varchar(128) NOT NULL UNIQUE,
    owner_id bigint NOT NULL REFERENCES user_info(id),
    data_stream_name varchar(128) NOT NULL,
    control_stream_name varchar(128) NOT NULL,
    activemq_url varchar(128) NOT NULL,
    primary key(id)
);


CREATE TABLE session_users
(
    session_id bigint NOT NULL REFERENCES session(id),
    user_id bigint NOT NULL REFERENCES user_info(id),
    primary key(session_id,user_id)
);

CREATE TABLE session_permission_groups
(
    session_id bigint NOT NULL REFERENCES session(id),
    permission_group_id bigint NOT NULL REFERENCES permission_group(id),
    primary key(session_id,permission_group_id)
);


CREATE TABLE session_datasource
(
    session_id bigint NOT NULL REFERENCES session(id),
    datasource_id bigint NOT NULL REFERENCES datasource(id),
    primary key(session_id,datasource_id)
);


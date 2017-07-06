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


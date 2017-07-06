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

CREATE TABLE datasource_option
(
    id bigserial,
    datasource_id bigint NOT NULL REFERENCES datasource(id),
    option_name varchar(128) NOT NULL,
    option_value varchar(1024) NOT NULL,
    UNIQUE (datasource_id,option_name),
    primary key(id)
);


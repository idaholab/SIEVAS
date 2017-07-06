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


CREATE OR REPLACE FUNCTION POPULATE_SESSION() RETURNS VOID AS $$
DECLARE
    sessionId bigint;
    userId bigint;
    datasourceId bigint;
BEGIN
    userId:=id FROM user_info WHERE username = 'user';

    datasourceId:=id FROM datasource WHERE name = 'NBody Datasource';
    INSERT INTO session(name,owner_id,data_stream_name,control_stream_name,activemq_url) VALUES('NBody Session', userId, '','','');
    sessionId:=LASTVAL();
    UPDATE session SET data_stream_name = 'data' || sessionId, control_stream_name = 'control' || sessionId WHERE id = sessionId;
    INSERT INTO session_datasource(session_id,datasource_id) VALUES(sessionId,datasourceId);

    datasourceId:=id FROM datasource WHERE name = 'UAV Datasource';
    INSERT INTO session(name,owner_id,data_stream_name,control_stream_name,activemq_url) VALUES('UAV Session', userId, '','','');
    sessionId:=LASTVAL();
    UPDATE session SET data_stream_name = 'data' || sessionId, control_stream_name = 'control' || sessionId WHERE id = sessionId;
    INSERT INTO session_datasource(session_id,datasource_id) VALUES(sessionId,datasourceId);
    
    datasourceId:=id FROM datasource WHERE name = 'Water Security Datasource';
    INSERT INTO session(name,owner_id,data_stream_name,control_stream_name,activemq_url) VALUES('Water Security Testbed Session', userId, '','','');
    sessionId:=LASTVAL();
    UPDATE session SET data_stream_name = 'data' || sessionId, control_stream_name = 'control' || sessionId WHERE id = sessionId;
    INSERT INTO session_datasource(session_id,datasource_id) VALUES(sessionId,datasourceId);

    

END
$$
LANGUAGE plpgsql VOLATILE;


SELECT POPULATE_SESSION();

DROP FUNCTION POPULATE_SESSION();


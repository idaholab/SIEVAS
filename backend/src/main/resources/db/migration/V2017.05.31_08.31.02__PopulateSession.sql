/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: May 31, 2017
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


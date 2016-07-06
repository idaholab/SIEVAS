/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Jul 1, 2016
 */

CREATE OR REPLACE FUNCTION POPULATE_USERS() RETURNS VOID AS $$
DECLARE
    userId bigint;
    testId bigint;
    userRoleId bigint;
    adminRoleId bigint;
    userGroupId bigint;
    adminGroupId bigint;
    cnt int;
BEGIN
    userId:=id FROM user_info WHERE username = 'user';
    /*RAISE NOTICE '%', userId;*/
    IF userId IS NULL THEN
        INSERT INTO user_info(username, password, first_name, last_name,expired, locked, enabled)
        VALUES('user','$2a$06$XM3MyO1BLCfOG5LtRz2zu..G1rJEtO0ygsE.7a29De33gGrCd8dbm','User','User', '0','0','1');
        /* NOTE: The password is BCrypt encrypted */
        userId:=LASTVAL();
    end if;

    testId:=id FROM user_info WHERE username = 'test';
    IF testId IS NULL THEN
        INSERT INTO user_info(username, password, first_name, last_name,expired, locked, enabled)
        VALUES('test','$2a$06$rXuXPrCpExX1EFPkhskmu.wGh5569aZcj59QhuURRQzWChriWqvyW','Test','User', '0','0','1');
        testId:=LASTVAL();
    END IF;

    userRoleId:=id FROM permission WHERE permission_name = 'user';
    IF userRoleId IS NULL THEN
        INSERT INTO permission(permission_name, description)
        VALUES('user','User Permission');
        userRoleId:=LASTVAL();
    END IF;

    adminRoleId:=id FROM permission WHERE permission_name = 'admin';
    IF adminRoleId IS NULL THEN
        INSERT INTO permission(permission_name, description)
        VALUES('admin','Administrator Permission');
        adminRoleId:=LASTVAL();
    END IF;

    userGroupId:=id FROM permission_group WHERE group_name = 'User Group';
    IF userGroupId IS NULL THEN
        INSERT INTO permission_group(group_name, description)
        VALUES('User Group','User Group');
        userGroupId:=LASTVAL();
    END IF;

    adminGroupId:=id FROM permission_group WHERE group_name = 'Admin Group';
    IF adminGroupId IS NULL THEN
        INSERT INTO permission_group(group_name, description)
        VALUES('Admin Group','Administrator Group');
        adminGroupId:=LASTVAL();
    END IF;

    cnt:=COUNT(*) FROM permission_group_permission 
        WHERE permission_group_id=userGroupId
        AND permission_id=userRoleId;
    IF CNT=0 THEN
        INSERT INTO permission_group_permission(permission_group_id, permission_id)
        VALUES(userGroupId,userRoleId);
    END IF;

    cnt:=COUNT(*) FROM permission_group_permission 
        WHERE permission_group_id=adminGroupId
        AND permission_id=userRoleId;
    IF CNT=0 THEN
        INSERT INTO permission_group_permission(permission_group_id, permission_id)
        VALUES(adminGroupId,userRoleId);
    END IF;

    cnt:=COUNT(*) FROM permission_group_permission 
        WHERE permission_group_id=adminGroupId
        AND permission_id=adminRoleId;
    IF CNT=0 THEN
        INSERT INTO permission_group_permission(permission_group_id, permission_id)
        VALUES(adminGroupId,adminRoleId);
    END IF;

    cnt:=COUNT(*) FROM user_info_permission_group 
        WHERE user_info_id=userId
        AND permission_group_id=userGroupId;
    IF CNT=0 THEN
        INSERT INTO user_info_permission_group(user_info_id, permission_group_id)
        VALUES(userId,userGroupId);
    END IF;

    cnt:=COUNT(*) FROM user_info_permission_group 
        WHERE user_info_id=userId
        AND permission_group_id=adminGroupId;
    IF CNT=0 THEN
        INSERT INTO user_info_permission_group(user_info_id, permission_group_id)
        VALUES(userId,adminGroupId);
    END IF;

    cnt:=COUNT(*) FROM user_info_permission_group 
        WHERE user_info_id=testId
        AND permission_group_id=userGroupId;
    IF CNT=0 THEN
        INSERT INTO user_info_permission_group(user_info_id, permission_group_id)
        VALUES(testId,userGroupId);
    END IF;

END;
$$
LANGUAGE plpgsql VOLATILE;


SELECT POPULATE_USERS();

DROP FUNCTION POPULATE_USERS();





/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Jun 30, 2016
 */

CREATE TABLE permission_group_permission
(
    permission_group_id bigint NOT NULL REFERENCES permission_group(id),
    permission_id bigint NOT NULL REFERENCES permission(id),
    PRIMARY KEY(permission_group_id,permission_id)
);


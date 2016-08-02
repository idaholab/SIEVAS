/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Jun 30, 2016
 */

CREATE TABLE user_info_permission_group
(
    user_info_id bigint NOT NULL REFERENCES user_info(id),
    permission_group_id bigint NOT NULL REFERENCES permission_group(id),
    PRIMARY KEY(user_info_id,permission_group_id)
);


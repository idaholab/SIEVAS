/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  monejh
 * Created: Sep 28, 2016
 */

create index nbody_idx_step on nbody(step);
create index nbody_idx_step_planet_num on nbody(step, planet_num);


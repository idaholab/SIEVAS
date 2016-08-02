#!/bin/bash
#
sudo -u postgres psql postgres -c '\password postgres'
createuser live -U postgres -W -h localhost
sudo -u postgres psql postgres -c "ALTER USER live WITH PASSWORD 'live';"
sudo su - postgres -c "psql postgres postgres -f - << EOF
create database live;
grant all on database live to live;
EOF
"


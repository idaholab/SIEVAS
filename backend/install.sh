#!/bin/bash
#
sudo -u postgres psql postgres -c '\password postgres'
createuser sievas postgres -W -h localhost
sudo -u postgres psql postgres -c "ALTER USER sievas WITH PASSWORD 'sievas';"
sudo su - postgres -c "psql postgres postgres -f - << EOF
create database sievas;
grant all on database sievas to sievas;
EOF
"


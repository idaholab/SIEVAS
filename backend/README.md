# **Ubuntu 14.04 Install Instructions**

## Installing NodeJS and npm
1. Ubuntu 14.04 comes pre-installed with outdated versions of NodeJS. These must be uninstalled before installing the latest version. To do so, use the following command:

        sudo apt-get purge nodejs*
        
    Be careful not to uninstall other programs you may need.

2. Then, install cURL:

        sudo apt-get install curl

3. Install the appropriate version of NodeJS (which includes npm):

        curl -sL https://deb.nodesource.com/setup_6.x | sudo bash -
        sudo apt-get install -y nodejs

## Installing PostgreSQL
1. Install the appropriate version of PostgreSQL using the following command:

        sudo apt-get install postgresql-9.3

2. Run the install.sh script located in the backend directory (AVL/SIEVAS/backend):

        cd SIEVAS/backend
        ./install.sh

## Testing the Build
1. Open a terminal, then build and run backend:

        cd SIEVAS/backend
        mvn

2. Open another terminal, then build and run SIEVASTestClient:

        cd SIEVAS/SIEVASTestClient
        mvn

3. The SIEVASTestClient should prompt you for a username and password. The username is 'user' and the password is 'password'. You will then be prompted to select a session from the available list. Enter the ID number of the desired session and hit enter. At this point, the terminal will display the date and time every five seconds. If this is the case, the login was a success.

## Creating a New Account
Navigate to https://localhost:8443. Log in with 'user' and 'password'. From here, it is possible to create new users and sessions.

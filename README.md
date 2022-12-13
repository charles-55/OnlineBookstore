# OnlineBookstore

A graphics-based online bookstore programmed using the Java programming language and postgresSQL as the backend database. 

## Authors
#### Osamudiamen Nwoko 
#### Oyindamola Taiwo-Olupeka


## Instructions For Setting Up The Database
There are different ways for you to set up the database:

### Option 1:
1. Create a database server in port 5433 on your device.
2. Create a user called "postgres" (done automatically by pgAdmin).
3. Set the password to "1234".
4. Add a new database called "COMP3005Project" to the server.

### Option 2:
- Create your database using your preferred information (modify the ConnectionManager according to your configuration).


## Instructions For Running The Application
There are different ways for you to run the application:

### Option 1 (Best):
1. Setup your database connection.
2. Make a pull request from this repository into your favourite version control IDE (e.g. Intellij, Visual Studio Code).
3. Modify the ConnectionManger.java class if you used option 2 to set up the database.
4. Run the code in the Main.java class.

### Option 2:
1. Setup your database connection using option 1.
2. Download the JAR file.
3. Open the JAR file location in your terminal.
4. Run the command "java OnlineBookstore.jar".

### Option 3:
1. Setup your database connection using option 2.
2. Download the JAR file and extract its contents.
3. Modify the ConnectionManager.java to have your configured database settings.
4. Run the Main.java class.


## For TA
- To sign in as an admin, the username is "TA" and the password is "123456".
- NOTE: Only admins can add new admins. There are previously defined admins.
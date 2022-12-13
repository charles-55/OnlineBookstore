# OnlineBookstore

### Authors
#### Osamudiamen Nwoko 

#### Oyindamola Taiwo-Olupeka 


### Instructions For Running The Application


There are different ways for you to run the the application:

Option 1:
- Go into the the JAR file and extract the src files
- Open the ConnectionManagar class in src folder and edit the DB_URL (line 26) which is static variable to be a database in your own pgAdmin so it is personalised for each user.
- If you have not already, create DataBase in your pgAdmin variable e.g "COMP3005project"
- Lastly, run main class.


Option 2:
- Create a database in your pgAdmin application called COMP3005project in port 5433 which has the exact url as what is written in the ConnectionManager
- The login details are as folows:
  USER = "postgres";
  PASSWORD = "1234";

- If you want to change the username and/or password, it would have to be edited in the ConnectionManager class (lines 29 & 30)

Option 3:




# Transactions

In order to run this project with Maven you will need the next command:

##### mvn clean package -DskipTests=true && mvn tomcat:run-war-only
#
### Usage:
#
##### POST http://localhost:8080/transactions/oauth/token?grant_type=password&username=adminUser&password=adminPass
#
With Basic Auth username: "transactions-client" and scret "secret" to obtain access and refresh tokens

Returns:
```json
{
    "access_token": <access_token>,
    "token_type": "bearer",
    "refresh_token": <refresh_token>,
    "expires_in": 119,
    "scope": "read write trust"
}
```
#

##### POST http://localhost:8080/transactions/oauth/token?grant_type=refresh_token&refresh_token=<refresh_token>
#
To obtain new access token when previous has expired.

##### GET http://localhost:8080/transactions/swagger-ui.html

To obtain all the info related to exposed API endpounts

##### GET http://localhost:8080/transactions/v1/current-accounts/transactions/?access_token=<access_token>

To obtain all the transactions.
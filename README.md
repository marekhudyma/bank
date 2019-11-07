# Bank in micronaut 

## How to build project
```
mvn clean install
```

## How to run project
```
java -jar target/bank-1.0.jar
```

## General concept: 
* I use Account with cached value of bank balance with `two side booking`, 
* BalanceChange stores balance change with a positive or negative value. Sum of balance changes will give a bank balance (equal to cached value in Account),
* Transfer connect two BalanceChanges - debtor is transferring money (negative BalanceChange) to creditor (positive balance change),
* While making transfer, both account are locked in database level (pessimistic write) `SELECT FOR UPDATE NOWAIT`,
* If another request will try to lock already locked account, it will immediately fail and client need to repeat request. This way I avoid deadlocks,
* I use Embedded PostgreSQL.

## What could be improved

* There is no health check, 
* There is no security,
* There is no metrics / alerts, 
* logs should have structural form (key=value),

## Interesting info
Generally I described locking mechanisms on my personal blog:
```
https://marekhudyma.com/sql/2018/10/01/database-locking.html
```
* in the past I wrote a core of the bank that was using idea of creation of Account + transaction in 1 request in a way, that it will not fail if account was not there, I described it here (I find it interesting):
```
https://marekhudyma.com/sql/2018/11/01/concurrent_calls.html
```
* In this project I used `Embedded PostgreSQL` - https://github.com/opentable/otj-pg-embedded
```
 Allows embedding PostgreSQL into Java application code with no external dependencies.
```
* I like to test real docker images, for example by using library testcontainers
```
https://marekhudyma.com/tests/2018/12/01/integration-tests-with-testcontainers.html
```
* Lombok doesn't fully work here. All dtos should have final fields, usually adding lombok.config with setting 'lombok.anyConstructor.addConstructorProperties=true' helped (I don't want to spend more time here)

## Example requests
### Create account
```
curl -i -X POST -H "Accept: application/json" \
-H "Content-Type: application/json" \
--data '{"owner": {"firstName":"firstName","lastName":"lastName"}}' \
http://localhost:8080/accounts
```
### Get account
```
curl -i -X GET -H "Accept: application/json" "http://localhost:8080/accounts/48d1dbfc-0ff9-4ff2-90f1-7f107a469859"
```
### Create transfer
```
curl -i -X POST -H "Accept: application/json" \
-H "Content-Type: application/json" \
--data '{"creditorId":"0bc07703-e0f0-4fb2-92fb-a4369953b30c","amount":"1.00"}' \
http://localhost:8080/accounts/48d1dbfc-0ff9-4ff2-90f1-7f107a469859/transfers
```
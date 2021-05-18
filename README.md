# Voting for a restaurant
## Task
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without a frontend**.
Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant, and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides a new menu each day.

## Install
```console
git clone https://github.com/vinsent1907/vote-for-restaurant.git
```
```console
$ mvn clean package
$ mvn install
```

----

**Note!** You must have installed **JDK 15** and **Maven**.

----


### Authorization
Use http basic authorization.
####credentials:
```console
name:   login:          password:
admin   admin@mail.com  admin  
user    user@mail.com   password
user2   user2@mail.com  password
```
# API

----

**Note!** operations curl for Windows console

----


[comment]: <> (You can see full REST API documentation at [/api-docs]&#40;http://localhost:8080/swagger-ui/index.html&#41; after deploy.)
[comment]: <> (Access to documentation allowed only for admin profile.)

##Operations with account
#### get current account:
`curl -s -i http://localhost:8080/api/account --user user@mail.com:user`

#### create account:
`curl -i -X POST -H "Content-Type: application/json" -d "{\"email\":\"user3@mail.com\",\"firstName\":\"Noname\",\"lastName\":\"Noname_Last\",\"password\": \"user\"}" http://localhost:8080/api/account/register`
#### edit account:
`curl -i -X PUT -H "Content-Type: application/json" -d "{\"email\":\"user3@mail.com\",\"firstName\":\"Noname\",\"lastName\":\"Noname_Last\",\"password\": \"user\"}" http://localhost:8080/api/account --user user@mail.com:user`
#### delete account:
`curl -s -i -X DELETE http://localhost:8080/api/account --user user@mail.com:user --user user@mail.com:user`


## Operations with restaurant
###  for only admin account:
#### get all restaurant:
`curl -s -i http://localhost:8080/api/restaurants --user admin@mail.com:admin`

#### create a restaurant:
`curl -i -X POST -H "Content-Type: application/json" -d "{\"name\": \"Late\"}" http://localhost:8080/api/restaurants/created --user admin@mail.com:admin`

#### edit restaurant:
`curl -i -X PUT -H "Content-Type: application/json" -d "{\"name\": \"Apelsino\"}" http://localhost:8080/api/restaurants/1 --user admin@mail.com:admin`

#### a deleted restaurant:
`curl -s -i -X DELETE http://localhost:8080/api/restaurants/1 --user user@mail.com:user --user admin@mail.com:admin`


### for user account:
#### get one restaurant with the current menu:
`http://localhost:8080/api/restaurants/1`

#### get all restaurant with today menu:
`curl -s -i http://localhost:8080/api/restaurants/today --user user@mail.com:password`

## Operations with menu
###  for only admin account:
#### create menu:
`curl -i -X POST -H "Content-Type: application/json" -d "{\"date\": \"2021-05-08\", \"description\": \"Pasta\", \"price\": 5555}" http://localhost:8080/api/restaurants/1/menus/created --user admin@mail.com:admin`

#### deleted menu:
`curl -s -i -X DELETE http://localhost:8080/api/restaurants/1/menus/delete/1 --user admin@mail.com:admin`

#### edit menu:
`curl -i -X PUT -H "Content-Type: application/json" -d "{\"date\": \"2021-05-08\", \"description\": \"Pasta\", \"price\": 5555}" http://localhost:8080/api/restaurants/1/menus/edit/2 --user admin@mail.com:admin`


## Operations with vote

#### vote for the restaurant:
`curl -s -i -X POST http://localhost:8080/api/votes/vote/?restaurantId=1 --user user@mail.com:user`

#### get current user vote:
`curl -s -i http://localhost:8080/api/votes/vote --user user@mail.com:user`

#### get today votes:
`curl -s -i http://localhost:8080/api/votes/today --user user@mail.com:user` 
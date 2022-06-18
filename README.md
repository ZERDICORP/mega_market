# mega_market
#### Test task for admission to the Yandex backend development school.

## What is this project about?
Have you ever had to surf the Internet looking for bargain shops?  
Or maybe you are annoyed by opaque price changes?  
Maybe you want to finally take matters into your own hands and control the search for offers?  

**Then you are at the right place!**  

**Mega Market** can provide you:
1. Adding products/categories;
2. Deleting products/categories;
3. Search for products/categories;
4. Search for products in the last 24 hours;
5. Obtaining a history of product/category changes.

## How to start the project?

#### 1. Make sure you have mariadb installed
```
$ mysql --version
mysql  Ver 15.1 Distrib 10.7.3-MariaDB, for Linux (x86_64) using readline 5.1
```

#### 2. Create a databases called `mega_market` and `mega_market_test`
```
$ mysql -u root -p
MariaDB> create database mega_market;
MariaDB> create database mega_market_test;
MariaDB> exit;
```

#### 3. Add file `src/main/resources/application.properties` with the following content
> Replace all values between <>.
```
# Database Configuration
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/mega_market?autoReconnect=true
spring.datasource.username=<DATABASE_USER>
spring.datasource.password=<DATABASE_USER_PASSWORD>

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect

# Spring Server Configuration
server.port=8000
server.error.include-message=always
```

#### 4. Add file `src/test/resources/test.properties` with the following content
> Replace all values between <>.
```
# Database Configuration
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/mega_market_test?autoReconnect=true
spring.datasource.username=<DATABASE_USER>
spring.datasource.password=<DATABASE_USER_PASSWORD>

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect

# Spring Server Configuration
server.port=8000
server.error.include-message=always
```

#### 5. Now you can run the project :tada:

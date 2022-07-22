# mega_market :moneybag:
#### Test task for admission to the Yandex backend development school.

## What is this project about? :raising_hand:
_Have you ever had to surf the Internet looking for **bargain shops**?  
Or maybe you are annoyed by **opaque price** changes?  
Maybe you want to finally take matters into your own hands and **control** the search for **offers**?_

**Then you are at the right place!**

**Mega Market** can provide you:
1. Adding offers/categories;
2. Deleting offers/categories;
3. Search for offers/categories;
4. Search for offers in the last 24 hours;
5. Obtaining a history of offer/category changes.

## Technology stack :wrench:
* ***Java*** (v17.0.3)
  - ***Spring-Boot*** (v2.7.0)
* ***MariaDB*** (v10.8.3)
* ***Apache Maven*** (v3.8.4)
* ***Docker*** (v20.10.17)
* ***Nginx*** (v1.22.0)

## Deployment guide :rocket:

#### 1. Make sure you have all dependencies installed
```
$ docker --version
Docker version 20.10.17, build 100c701
```
```
$ nginx -version
nginx version: nginx/1.22.0
```
```
$ mvn --version
Apache Maven 3.8.6 (84538c9988a25aec085021c365c560670ad80f63)
Maven home: /opt/maven
Java version: 17.0.3, vendor: Private Build, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-26-generic", arch: "amd64", family: "unix"
```

#### 2. Clone this repository
```
$ git clone https://github.com/ZERDICORP/mega_market.git
```

#### 3. Creating and configuring mariadb docker container
```
$ cd src/main/resources/mariadb_docker_init
```
```
$ docker run --restart=unless-stopped -p 3306:3306 -d --name mariadb -eMARIADB_ROOT_PASSWORD=pass mariadb:latest
```
```
$ docker exec -i mariadb sh -c 'mysql --host=localhost --user=root --password=pass' < 01-user-setup.sql && docker exec -i mariadb sh -c 'mysql --host=localhost --user=root --password=pass' < 02-database-setup.sql
```

#### 4. Project configuration
```
$ cd <PATH_TO_PROJECT_ROOT>
```
> Replace all values between **<>**.
```
$ vim src/main/resources/application.properties
# Database Configuration
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/mega_market?autoReconnect=true
spring.datasource.username=<DATABASE_USER>
spring.datasource.password=<DATABASE_PASSWORD>

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect

# Spring Server Configuration
server.port=8000
server.error.include-message=always

# MVC Configuration
spring.mvc.format.date-time=iso
```
> Replace all values between **<>**.
```
$ vim src/test/resources/test.properties
# Database Configuration
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/mega_market_test?autoReconnect=true
spring.datasource.username=<DATABASE_USER>
spring.datasource.password=<DATABASE_PASSWORD>

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect

# Spring Server Configuration
server.port=8000
server.error.include-message=always
```

#### 5. Running a project in a docker container
```
$ mvn package
```
```
$ docker build -t mega_market .
```
```
$ docker run --restart=unless-stopped -d --network=host --name mega_market_container mega_market
```

#### 6. Nginx server configuration
```
$ sudo vim /etc/nginx/nginx.conf
user ubuntu;
  
worker_processes 1;

events {
  worker_connections  1024;
}


http {
  include mime.types;
  
  default_type application/octet-stream;
  sendfile on;
  keepalive_timeout 65;
  
  server {
    listen 80;
    server_name localhost;
    
    location / {
      if ($request_method = 'OPTIONS') {
        add_header 'Access-Control-Allow-Origin' '*';
        add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authentication-Token';
        add_header 'Access-Control-Max-Age' 1728000;
        add_header 'Content-Type' 'text/plain; charset=utf-8';
        add_header 'Content-Length' 0;
        return 204;
      }
      if ($request_method = 'POST') {
        add_header 'Access-Control-Allow-Origin' '*' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authentication-Token' always;
        add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
      }
      if ($request_method = 'GET') {
        add_header 'Access-Control-Allow-Origin' '*' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authentication-Token' always;
        add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
      }
      
      proxy_pass http://127.0.0.1:8000;
    }
  }
}
```
> Don't forget to restart nginx.
```
$ sudo systemctl restart nginx
```

#### 7. The project is now available on port `80` :confetti_ball:

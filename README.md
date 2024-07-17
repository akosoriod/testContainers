# This app will sync snowflake tables data to a REDIS cache
At the moment it simply dumps all records from a table and add them to REDIS using a transaction
# Dev setup steps

## Install dependencies

### Java 17

### Gradle
https://docs.gradle.org/current/userguide/installation.html

### Kotlin compiler
https://github.com/JetBrains/kotlin/releases/download/v2.0.0/kotlin-compiler-2.0.0.zip

## Set up snowflake connection auth vars

Simply export this variables in your local env, set appropriate values:
```
export SNOWFLAKE_ACCOUNT_NAME="jruyegl-wpb53391"
export SNOWFLAKE_DB_NAME="SAMPLE_DB"
export SNOWFLAKE_WAREHOUSE_NAME="COMPUTE_WH"
export SNOWFLAKE_ROLE_NAME="DEVELOPER"
export SNOWFLAKE_USERNAME="username"
export SNOWFLAKE_PASSWORD="password"
export SNOWFLAKE_TABLE_NAME="USER_VEHICLE_DATA"
```

**Before running the program with those env vars you can test a connection using snowsql CLI tool**

## Set up and run REDIS in local machine

### Install docker

### Install redis-tools
```
sudo apt install redis-tools
```

### Run redis container
```
docker run --name redis -p 6379:6379 -d redis
```

## Test local app

### Start spring app
```
./gradlew bootRun
```

### Send commands to app ennpoints
```
curl http://localhost:8080/api/v1/sync
```
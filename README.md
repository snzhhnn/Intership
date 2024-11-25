# MigrationLibrary

Database Migration Library — It is a tool for managing database migrations in Java projects. The library makes it easy to create, manage, and perform migrations, ensuring stability and reproducibility of database structure changes.

## Opportunities

- **Applying migrations**: Performing migrations in the specified sequence.
- **Rollback migrations**: The ability to cancel one or more migrations.
- **Logging**: Logging of completed operations.
- **Support for various DBMS**: Works with MySQL, PostgreSQL and H2 databases.

## Installation

Add the library to your project using Maven or Gradle.

### Maven

```
<dependencies>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>Migration</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

### Gradle

```
implementation 'com.example:Migration:1.0-SNAPSHOT'
```

## Using

**Step 1: Configuring the database connection**
Create the application.properties configuration file

```
db.url=jdbc:postgresql://localhost:5432/mydb
db.username=myuser
db.password=mypassword
```

**Step 2: Create a migration file**
Migrations are SQL scripts. For example, the V1 migration file__create_users_table.sql:

```
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
```
**Step 3: Perform migrations**
Use the library API to perform migrations:

```
public class Main {
    public static void main(String[] args) {
        MigrationTool.start();
    }
}
```

## Feedback
If you have questions, ideas, or you find a bug, let me know! 
Create an issue in this repository or submit an extraction request. 🎀



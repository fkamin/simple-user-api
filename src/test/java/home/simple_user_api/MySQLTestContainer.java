package home.simple_user_api;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLTestContainer {
    private static final DockerImageName MYSQL_IMAGE_NAME = DockerImageName.parse("mysql:8.4.6");

    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>(MYSQL_IMAGE_NAME)
                    .withDatabaseName("test-database")
                    .withUsername("test-user")
                    .withPassword("test-password");

    static {
        mysqlContainer.start();
    }

    public static MySQLContainer<?> getInstance() {
        return mysqlContainer;
    }
}

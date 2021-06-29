package com.vinylteam.vinyl;

import org.testcontainers.containers.PostgreSQLContainer;

public class WaxDealsPostgresqlContainer extends PostgreSQLContainer<WaxDealsPostgresqlContainer> {

    private static WaxDealsPostgresqlContainer container;

    private WaxDealsPostgresqlContainer() {
        super(IMAGE);
    }

    public static WaxDealsPostgresqlContainer getInstance() {
        if (container == null) {
            container = new WaxDealsPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
    }

}

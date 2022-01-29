package at.rajoub.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseConfigTest {
    @Test
    void getInstance_true() {
        Assertions.assertNotNull(DataBaseConfig.getInstance());
    }

}
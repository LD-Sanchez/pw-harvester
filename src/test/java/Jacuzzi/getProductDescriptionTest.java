package Jacuzzi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class getProductDescriptionTest {

    @Test
    public static String getProductDescription() {
        String description = "This is the product description";
        if (!description.isEmpty()) {
            return description;
        }
        else return fail();
    }
}
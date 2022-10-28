package Jacuzzi;

import org.junit.jupiter.api.Test;

import static Jacuzzi.getProductDescriptionTest.getProductDescription;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MainTest {

    @Test
    void harvestProductListings() {
        MarketListing mockProduct = mock(MarketListing.class);
        mockProduct.description = getProductDescription();
        assertEquals(mockProduct.description, "This is the product description", "Test passed" );
    }

    @Test
    void saveProductsToCsv() {
        fail("not yet implemented");
    }
}
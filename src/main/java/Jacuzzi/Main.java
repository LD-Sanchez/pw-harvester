package Jacuzzi;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class MarketListing {
    String name;
    String description;
    String price;
    String listingUrl;
    String imageUrl;
    int orderOnPage;
    boolean paidSearch;
}

public class Main {
    public static void main(String[] args) throws IOException {
        harvestProductListings();
    }

    public static void harvestProductListings() throws IOException {
        ArrayList<MarketListing> marketListings = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.webkit().launch();
            Page page = browser.newPage();
            page.navigate("https://www.etsy.com/search?q=jacuzzi");
            List<ElementHandle> productList = page.querySelectorAll(".v2-listing-card");
            //System.out.println(productList.stream().count());
            int index = 0;
            for (ElementHandle product : productList) {
                int imgIndex = index + 1;
                String imgClass = ".sr_gallery-1-" + imgIndex;
                String temp;
                MarketListing listing = new MarketListing();
                listing.listingUrl = product.querySelector(".listing-link").getAttribute("href");
                listing.name = product.querySelector(".v2-listing-card__title").innerHTML();
                temp = getProductDescription(listing.listingUrl, browser);
                listing.description = temp
                        .replace("\n", "")
                        .replaceAll(",", " ");
                String price = product.querySelector(".currency-value").innerHTML();
                listing.price = price;
                listing.imageUrl = product.querySelector(".wt-width-full").getAttribute("src");
                listing.orderOnPage = index + 1;
                listing.paidSearch = product.querySelector("span:has-text('ad ')").isVisible();
                marketListings.add(listing);
                index++;
                if (index == 10) break;
            }
        }
        saveProductsToCsv(marketListings);
    }

    public static String getProductDescription(String url, Browser browser) {
        Page page = browser.newPage();
        page.navigate(url);
        ElementHandle descriptionBox = page.querySelector("#wt-content-toggle-product-details-read-more");
        String description = descriptionBox.querySelector(".wt-text-body-01").innerText();
        return description;
    }

    public static void saveProductsToCsv(List<MarketListing> marketListings) throws IOException {
        System.out.println("Writing csv file");
        String filePath = "/tmp/csv/products.csv";//"/Users/luis.sanchez/IdeaProjects/Pw-Harvester/src/files/products.csv";

        File productsCsv = new File(filePath);
        //productsCsv.getParentFile().mkdirs(); //Creates parent directories in case they do not exist
        FileOutputStream oFile = new FileOutputStream(productsCsv, true);
        try {
            // create a writer
            Writer writer = new FileWriter(filePath);//Files.newBufferedWriter(Paths.get("/Users/luis.sanchez/IdeaProjects/Pw-Harvester/src/files/file.csv"));
            HeaderColumnNameMappingStrategy<MarketListing> strategy = new HeaderColumnNameMappingStrategyBuilder<MarketListing>().build();
            strategy.setType(MarketListing.class);
            //strategy.setColumnOrderOnWrite(new MyComparator());
            // create a csv writer
            StatefulBeanToCsv<MarketListing> csvWriter = new StatefulBeanToCsvBuilder<MarketListing>(writer)
                    .withSeparator('§')
                    //.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    //.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    //.withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    //.withOrderedResults(true)
                    .withMappingStrategy(strategy)
                    .build();
            csvWriter.write(marketListings);
            // close the writer
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        }
    }
}




import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class AmazonCartTest {

    public static void main(String[] args) {
        // Set ChromeDriver path
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\saket\\Desktop\\UCM\\ASE\\chromedriver-win64\\chromedriver.exe");

        // Configure ChromeOptions for WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Maximize the browser window
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open Amazon website
            driver.get("https://www.amazon.com");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Implicit wait

            // Perform search for a product
            WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
            searchBox.sendKeys("Thinking, Fast and Slow");
            searchBox.submit();

            // Wait for search results and click on the first product
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement firstProductLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='a-size-medium a-color-base a-text-normal']")));
            firstProductLink.click();

            // Switch to new tab if opened (if needed)
            String currentTab = driver.getWindowHandle();
            for (String tab : driver.getWindowHandles()) {
                if (!tab.equals(currentTab)) {
                    driver.switchTo().window(tab);
                    break;
                }
            }

            // Wait for product page to load and fetch product price
            WebElement productPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"a-autoid-3-announce\"]/span[2]/span")));
            String productPriceText = productPriceElement.getText().trim();

            // Clean and convert product price to double for comparison
            double productPrice = parsePrice(productPriceText);

            // Add product to cart
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
            addToCartButton.click();

            // Wait for the cart icon to be clickable and click on it
            WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart")));
            cartIcon.click();

            // Click on the quantity dropdown to expand options
            WebElement quantityDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@id='a-autoid-1-announce']")));
            quantityDropdown.click();

            // Select quantity 6 from the dropdown options
            WebElement quantityOption6 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='quantity_6']")));
            quantityOption6.click();

            // Print the selected quantity
            String selectedQuantity = quantityOption6.getText().trim();
            System.out.println("Selected Quantity: 6 ");

            // Check for coupon code option
            boolean couponCodeAvailable = isCouponCodeSectionPresent(driver);

            if (couponCodeAvailable) {
                System.out.println("Coupon code section is available in the cart.");
            } else {
                System.out.println("Coupon code section is not available in the cart.");
            }
            WebElement cartPriceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"sc-subtotal-amount-buybox\"]/span")));
            String cartPriceText = cartPriceElement.getText().trim();
            double cartPrice = parsePrice(cartPriceText);

            // Compare product price with cart price (if needed)
            double tolerance = 0.01; // Tolerance for price comparison
            if (Math.abs(productPrice - cartPrice) < tolerance) {
                System.out.println("Price verification successful!  Product Price: " + productPrice + " Cart Price: " + cartPrice);
            } else {
                System.out.println("Price mismatch: Product price = " + productPrice + ", Cart price = " + cartPrice);
            }

            // Delete the product from the cart
            WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Delete']")));
            deleteButton.click();

            // Verify if cart is empty
            WebElement emptyCartMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[contains(text(), 'Your Amazon Cart is empty.')]")));
            System.out.println("Cart is empty: " + emptyCartMessage.getText().trim());

            // Optionally, fetch cart subtotal price for comparison


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Quit the WebDriver instance
           // driver.quit();
        }
    }

    // Helper method to parse price from string to double
    private static double parsePrice(String priceText) {
        String cleanedPrice = priceText.replaceAll("[^\\d.]", "");
        return Double.parseDouble(cleanedPrice);
    }

    // Helper method to check if coupon code section is present in the cart
    private static boolean isCouponCodeSectionPresent(WebDriver driver) {
        try {
            WebElement couponCodeInput = driver.findElement(By.id("gc-redemption-input"));
            return couponCodeInput.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}

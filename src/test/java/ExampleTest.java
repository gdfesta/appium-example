import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * @author gdfesta
 */
public class ExampleTest {
    private static AppiumDriver<MobileElement> driver;
    private static String platform;

    @Parameters({"platform"})
    @BeforeClass
    public void classSetup(@Optional(MobilePlatform.IOS) String platformSelected) {
        platform = platformSelected;
        if (isAndroid()) {
            try {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.gdfesta.automation_appium");
                capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.gdfesta.automation_appium.MainActivity");
                capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Nexus_5X_API_22_5.1_");

                driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not create Appium Driver for Android: " + ex.getLocalizedMessage());
            }
        } else {
            try {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.gdfesta.automation-appium");
                capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 6");
                capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.3");
                capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");

                driver = new IOSDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not create Appium Driver for iOS: " + ex.getMessage(), ex);
            }
        }
    }

    @Test
    public void testSetup() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOf(findElementByName("Text 99")));

        int end = 50;
        findElementScrolling(99, end).tap(1,10);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.id("detail_text"))));
        assertTrue(findElementByName("Detail for Text " + end).isDisplayed());
    }

    private static MobileElement findElementScrolling(Integer init, Integer end) {
        MobileElement element = findElementByName("Text " + end);
        Point location = element.getLocation();
        if (location.getX() == 0 && location.getY() == 0) {
            Point initElement = findElementByName("Text " + init).getLocation();
            Point locationIntermediate = findElementByName("Text " + (init - 10)).getLocation();
            driver.swipe(locationIntermediate.getX(), locationIntermediate.getY(), initElement.getX(), initElement.getY(), 1);
            return findElementScrolling(init - 10, end);
        }
        return element;
    }

    private static MobileElement findElementByName(String name) {
        if (isAndroid()) {
            return driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(.scrollable(true)).scrollIntoView(new UiSelector().textContains(\"" + name + "\"))"));
        } else {
            return driver.findElement(MobileBy.name(name));
        }
    }

    private static boolean isAndroid() {
        return MobilePlatform.ANDROID.equals(platform);
    }
}
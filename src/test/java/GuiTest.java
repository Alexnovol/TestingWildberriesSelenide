import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static steps.asserts.Gui.shouldBeEquals;
import static utils.Locators.*;

public class GuiTest {

    private final String LINK = "https://www.wildberries.ru/";


    @BeforeAll
    public static void setUp() {

        WebDriverManager.chromedriver().setup();

        Configuration.timeout = 30000;

    }

    @BeforeEach
    public void cleanCookies() {
        Selenide.clearBrowserCookies();
    }

    @Test
    @DisplayName("Работа с поисковой строкой")
    public void checkSearchInput() {

        open(LINK);

        $x(XPATH_MAIN_PAGE_CONTENT).shouldBe(enabled);

        SelenideElement searchInput = $(ID_SEARCH_INPUT);
        searchInput.click();
        searchInput.setValue("Iphone 13");
        searchInput.sendKeys(Keys.ENTER);

        $(CLASS_TITLE_RESULTS).shouldHave(text("По запросу Iphone 13 найдено"));

        $x(XPATH_FIRST_FILTER).shouldHave(text("iphone 13"));

        $x(XPATH_SECOND_FILTER).shouldHave(text("По популярности"));

        $x(XPATH_FIRST_PRODUCT + XPATH_BRAND_FIRST_PRODUCT).shouldHave(text("Apple"));

        $(CSS_BUTTON_CLEAR_SEARCH_FIELD).click();

        searchInput.shouldHave(value(""));

    }

    @Test
    @DisplayName("Смена города")
    public void changeCity() {

        open(LINK);

        $x(XPATH_MAIN_PAGE_CONTENT).shouldBe(enabled);

        SelenideElement buttonChangeCity = $x(XPATH_BUTTON_CHANGE_CITY);
        buttonChangeCity.click();

        SelenideElement searchInput = $x(XPATH_ADDRESS_ENTRY);
        searchInput.click();
        searchInput.setValue("Санкт-Петербург");

        $x(XPATH_BUTTON_FIND).click();

        $x(XPATH_ANY_DELIVERY_POINT).shouldBe(visible);

        SelenideElement firstAddress = $x(XPATH_FIRST_DELIVERY_POINT);
        String strFirstAddress = firstAddress.$x(XPATH_ADDRESS_FIRST_DELIVERY_POINT).text();
        firstAddress.click();

        SelenideElement infoDeliveryPoint = $(CLASS_INFO_DELIVERY_POINT);
        infoDeliveryPoint.shouldBe(visible);

        $(CLASS_ADDRESS_DELIVERY_POINT).shouldHave(text(strFirstAddress));

        $(CSS_BUTTON_SELECT_DELIVERY_POINT).click();

        infoDeliveryPoint.should(disappear);

        buttonChangeCity.shouldHave(text(strFirstAddress));

    }

    @Test
    @DisplayName("Добавление товара в корзину")
    public void addProductBasket() {

        open(LINK);

        $x(XPATH_MAIN_PAGE_CONTENT).shouldBe(enabled);

        $(CSS_BUTTON_FILTERS).click();

        $(CSS_CATEGORY_MENU).hover();

        SelenideElement houseAppliances = $(CSS_HOUSE_APPLIANCES);
        houseAppliances.scrollTo();
        houseAppliances.hover();

        $x(XPATH_APPLIANCES_FOR_HOUSE).click();

        $x(XPATH_CATEGORY_HOOVERS_AND_STEAM_CLEANERS).click();

        SelenideElement robotHoovers = $x(XPATH_CATEGORY_ROBOT_HOOVERS);
        String textRobotHoovers = robotHoovers.text();
        robotHoovers.click();

        $(CLASS_CATALOG_TITLE).shouldHave(text(textRobotHoovers));

        List<String> expectedFilters = Arrays.asList("Главная", "Бытовая техника", "Техника для дома",
                "Пылесосы и пароочистители", "Роботы-пылесосы");
        List<String> actualFilters = new ArrayList<>();

        for (SelenideElement filter : $$(CLASS_FILTERS_ON_PAGE)) {
            String textFilter = filter.$("span").text();
            actualFilters.add(textFilter);
        }

        shouldBeEquals(expectedFilters, actualFilters);

        SelenideElement firstProduct = $x(XPATH_FIRST_PRODUCT);
        String firstProductName = firstProduct.$x(XPATH_NAME_FIRST_PRODUCT).text().replace("/ ", "");
        String firstProductPrice = firstProduct.$x(XPATH_PRICE_FIRST_PRODUCT).text();

        SelenideElement inBasketButton = firstProduct.$x(XPATH_IN_BASKET_BUTTON);
        inBasketButton.scrollTo();
        inBasketButton.click();

        $(CLASS_COUNT_PRODUCTS_BASKET).shouldHave(text("1"));

        $x(XPATH_BASKET_BUTTON).click();

        Selenide.sleep(1000);

        $(CLASS_NAME_PRODUCT).shouldHave(text(firstProductName));
        $(CLASS_PRICE_PRODUCT).shouldHave(text(firstProductPrice));

        $x(XPATH_PRICE_TOTAL).shouldHave(text(firstProductPrice));

        $(CSS_ORDER_BUTTON).shouldBe(enabled);

    }

    @Test
    @DisplayName("Работа с фильтрами")
    public void checkFilters() {

        open(LINK);

        $x(XPATH_MAIN_PAGE_CONTENT).shouldBe(enabled);

        $(CSS_BUTTON_FILTERS).click();

        $(CSS_CATEGORY_MENU).hover();

        $x(XPATH_ELECTRONICS_CATEGORY).hover();

        $x(XPATH_LAPTOPS_AND_COMPUTERS_CATEGORY).click();

        $x(XPATH_LAPTOPS_CATEGORY).click();

        $(CSS_BUTTON_ALL_FILTERS).click();

        String priceFrom = "100 000";
        SelenideElement filterPriceFrom = $x(XPATH_FILTER_PRICE_FROM);
        filterPriceFrom.clear();
        filterPriceFrom.setValue(priceFrom);

        String priceTo = "149 000";
        SelenideElement filterPriceTo = $x(XPATH_FILTER_PRICE_TO);
        filterPriceTo.clear();
        filterPriceTo.setValue(priceTo);

        String brandText = $x(XPATH_FILTER_BRAND_TEXT).getOwnText().replaceAll("\\W", "");
        String diagonalText = $x(XPATH_FILTER_DIAGONAL_TEXT).getOwnText();
        diagonalText = diagonalText.substring(0, diagonalText.indexOf("\"") + 1);

        $x(XPATH_FILTER_BRAND).click();

        $x(XPATH_FILTER_DIAGONAL).click();

        Selenide.sleep(2000);

        String countProducts = $(CLASS_COUNT_FILTERED_GOODS_IN_FILTERS).text()
                .replaceAll("\\D+", "");

        $(CSS_BUTTON_SHOWING).click();

        String countProductsOnPage = $x(XPATH_COUNT_FILTERED_GOODS_ON_PAGE).text().replaceAll(" ", "");

        shouldBeEquals(countProducts, countProductsOnPage);

        $x(XPATH_SELECTED_FILTER_BRAND).shouldHave(text(brandText));

        $x(XPATH_SELECTED_FILTER_PRICE).shouldHave(text(String.format("от %s до %s", priceFrom, priceTo)));

        $x(XPATH_SELECTED_FILTER_DIAGONAL).shouldHave(text(diagonalText));

        $x(XPATH_BUTTON_RESET).shouldHave(enabled);
    }
}

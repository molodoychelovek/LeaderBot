package com.company.bot.bot.Parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.HashMap;

public class WebSurfing extends ArrayList<String>{
    private WebDriver driver = null;

    public WebSurfing(){
        prop();
    }

    private void prop(){
        System.setProperty("webdriver.geckodriver.driver", "geckodriver.exe"); // /usr/bin/geckodriver

       // ChromeOptions options = new ChromeOptions();
        /*options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--headless");
        options.addArguments("--no-proxy-server");
        options.addArguments("--disable-gpu");
        options.addArguments("start-maximized"); // open Browser in maximized mode
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems*/

        driver = new FirefoxDriver();
    }

    public HashMap<String, String> connect(String url){
        HashMap<String, String> hm = new HashMap<>();

        try {
            url = url.replace("https://auto.ru/cars/", "https://auto.ru/moskva/cars/");
            driver.get(url);
            System.out.println("Connect to " + url);

            try {
                driver.findElements(By.xpath("//div[@class='button button_blue']")).get(0).click();
                System.out.println("After accept");
            } catch (Exception e){
                System.out.println("Accept empty");
            }

            Thread.sleep(10000);
            try {
                driver.findElements(By.xpath("//span[@class='Button__content']")).get(0).click();
               Document document = Jsoup.parse(driver.getPageSource());
               Elements elements = document.getElementsByClass("Link CardDealerName-module__dealerName");

               hm.put("Результат", elements.get(0).attr("href"));
               System.out.println(hm.get("Результат"));
               hm.put("Конкурент", elements.get(0).text());
               System.out.println(hm.get("Конкурент"));

            } catch (Exception e) {
                hm.put("Результат", " ");
                hm.put("Конкурент", " ");
                e.printStackTrace();
            }

        } catch(Exception ex){
            try {
                driver.close();
            } catch (Exception e){}
            try {
                driver.quit();
            } catch (Exception e){ }
            prop();
            ex.printStackTrace();
        }

        return hm;
    }
}

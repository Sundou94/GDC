package gaker.hynixMenu;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@RestController
public class Application {

	@RequestMapping("/")
	public String home() {
		return "Hello Docker World";
	}

	@RequestMapping("/test")
	public String test() {
		return "api Test";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args).getBean(Application.class).crawling();
	}

	public void crawling() {
		System.setProperty("webdriver.chrome.driver","/Users/dousun/Desktop/study/Gaker/hynixMenu/path/to/ChromeDriver");

		ChromeOptions options = new ChromeOptions();

		options.setPageLoadStrategy(PageLoadStrategy.NONE);

		//ChromeDriver driver = new ChromeDriver(options);

		WebDriver driver = new ChromeDriver(options);

		int dayOfMonth = LocalDate.now().getDayOfMonth();

		try {
			driver.get("https://mc.skhystec.com/V3/menu.html");
			Thread.sleep(1500);
			driver.findElement(By.xpath("//*[@id=\"btnC_CJ\"]")).click();
			driver.findElement(By.xpath("//*[@id=\"btnR_12\"]")).click();
			driver.findElement(By.xpath("//*[@id=\"btnT_L\"]")).click();    //현재 청주 2캠 중식만 기능



			driver.findElement(By.xpath("/html/body/div[2]/section[2]/nav/p")).click(); //달력 열기
			List<WebElement> menuDateButton = driver.findElements(By.className("ui-state-default"));  //달력 a 하나하나..
			menuDateButton.get(dayOfMonth).click();

//            for(int i=0; i<=7; i++) {
//                menuDate.get(dayOfMonth+i).click(); //get(dayOfMonth) = 내일날짜 FIXME : 이부분은 메뉴 업데이트 되는 방식 알게되면..
//            }

			Thread.sleep(1500); //자바스크립트 로드 시간보다 자바 컴파일이 더 빨라 억지로 슬립을 줌..

			String menuOriginDate = driver.findElement(By.className("menu_date")).getAttribute("placeholder");
			String menuYear = menuOriginDate.substring(0,4);
			String menuMonth = menuOriginDate.substring(5,7);
			String menuDate = menuOriginDate.substring(8,10);
			String menuDay = menuOriginDate.substring(12,13);

			List<WebElement> menuList = driver.findElements(By.className("menu_list"));

			for (WebElement element : menuList) {
				String imgUrl = element.findElement(By.tagName("img")).getAttribute("src");
				String menuRestaurant = element.findElement(By.className("menu_restaurant")).getText();
				String[] menuName = element.findElement(By.className("menu_name")).getText().split(" - ");
				String mainMenu = menuName[0];
				String menuKcal = menuName[1];
				String menuCountryOfOrigin = element.findElement(By.className("menu_CountryofOrigin")).getText();
				List<WebElement> sideDish = element.findElements(By.cssSelector("ul.menu_sideDish_list_wrap > li"));

//                System.out.println(element.getText());

				System.out.println("------"+menuYear+"-"+menuMonth+"-"+menuDate+"-"+menuDay+"--------------------------");
				System.out.println("제조이미지 : " + imgUrl);
				System.out.println("메뉴종류 : " + menuRestaurant);
				System.out.println("메인메뉴 : " + mainMenu);
				System.out.println("총열량 : " + menuKcal);
				System.out.println("원산지 : " + menuCountryOfOrigin);

				for (WebElement webElement : sideDish) {
					System.out.println("사이드 : " + webElement.getText());
				}
				System.out.println("-----------------------------------");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}
}
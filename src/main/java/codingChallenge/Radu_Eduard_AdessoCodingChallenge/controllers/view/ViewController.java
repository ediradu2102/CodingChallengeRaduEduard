package codingChallenge.Radu_Eduard_AdessoCodingChallenge.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {
    @GetMapping("/home")
    public String home() {
        return "index";
    }
    @GetMapping("/data/{country}/{pastDays}")
    public String redirectToEarthquakeDataPage(@PathVariable String country, @PathVariable int pastDays) {
        return "earthquakeData";
    }
}
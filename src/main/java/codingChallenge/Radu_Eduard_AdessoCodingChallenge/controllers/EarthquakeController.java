package codingChallenge.Radu_Eduard_AdessoCodingChallenge.controllers;

import codingChallenge.Radu_Eduard_AdessoCodingChallenge.services.EarthquakeService;
import codingChallenge.Radu_Eduard_AdessoCodingChallenge.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/earthquakes")
public class EarthquakeController {

    private final EarthquakeService earthquakeService;
    private final GeocodingService geocodingService;

    @Autowired
    public EarthquakeController(EarthquakeService earthquakeService, GeocodingService geocodingService) {
        this.earthquakeService = earthquakeService;
        this.geocodingService = geocodingService;
    }

    @GetMapping("/country")
    public String getCountryByCoordinates(@RequestParam double latitude, @RequestParam double longitude) {
        return geocodingService.reverseGeocodeToCountry(latitude, longitude);
    }

    @GetMapping(value = "/byCountry")
    public List<String> getEarthquakesByCountry(@RequestParam String country, @RequestParam int pastDays) {
        return earthquakeService.getEarthquakesByCountryAndPastDays(country, pastDays);
    }

    @GetMapping("/hasEarthquakes")
    public boolean hasEarthquakes(@RequestParam String country, @RequestParam int pastDays) {
        return earthquakeService.hasEarthquakesInCountry(country, pastDays);
    }
}
package codingChallenge.Radu_Eduard_AdessoCodingChallenge.services;

import codingChallenge.Radu_Eduard_AdessoCodingChallenge.Apiresponse.EarthquakeResponse;
import codingChallenge.Radu_Eduard_AdessoCodingChallenge.modells.Earthquake;
import codingChallenge.Radu_Eduard_AdessoCodingChallenge.modells.GeocodingBoundingBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EarthquakeService {

    private final WebClient webClient;

    private final GeocodingService geocodingService;

    @Autowired
    public EarthquakeService(WebClient.Builder webClientBuilder, GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
        String usgsApiBaseUrl = "https://earthquake.usgs.gov/fdsnws/event/1/";
        this.webClient = webClientBuilder.baseUrl(usgsApiBaseUrl).build();
    }

    //Function that returns all the earthquakes in a country in the past given days
    //from the minimum latitude, and longitude and maximum latitude and longitude, of a country
    public List<Earthquake> ListOfEarthquakesInRegionInPastDays(int pastDays, double minLat, double maxLat, double minLon, double maxLon) {
        try {
            List<Earthquake> earthquakes = new ArrayList<>();

            Instant now = Instant.now();
            Instant startTime = now.minus(Duration.ofDays(pastDays));
            Instant endTime = now;

            String startTimeStr = startTime.toString().split("T")[0]; // Get the date part only
            String endTimeStr = endTime.toString().split("T")[0]; // Get the date part only

            String apiUrl = "query?format=geojson&starttime=" + startTimeStr + "&endtime=" + endTimeStr +
                    "&minlatitude=" + minLat + "&maxlatitude=" + maxLat +
                    "&minlongitude=" + minLon + "&maxlongitude=" + maxLon;

            Mono<EarthquakeResponse> earthquakeResponseMono = webClient
                    .get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(EarthquakeResponse.class);

            EarthquakeResponse earthquakeResponse = earthquakeResponseMono.block();
            if (earthquakeResponse != null && earthquakeResponse.getFeatures() != null) {
                for (EarthquakeResponse.Feature feature : earthquakeResponse.getFeatures()) {
                    String id = feature.getId();
                    double latitude = feature.getGeometry().getCoordinates()[1];
                    double longitude = feature.getGeometry().getCoordinates()[0];
                    String location = feature.getProperties().getPlace();
                    double magnitude = feature.getProperties().getMag();
                    String dateTime = feature.getProperties().getTime();
                    earthquakes.add(new Earthquake(id, latitude, longitude, location, magnitude, dateTime));
                }
            }
            return earthquakes;
        }catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching earthquake data", ex);
        }
    }

    //Function that returns a list of all the earthquakes in a given country in the past given days
    //The data is returned with the desired format
    public List<String> getEarthquakesByCountryAndPastDays(String country, int pastDays) {
        try {
            if (pastDays < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Past days should be a positive number");
            }
            GeocodingBoundingBox countryBounds = geocodingService.getCountryBoundingBox(country).block();

            if (countryBounds == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid country: " + country);
            }

            List<Earthquake> earthquakes = ListOfEarthquakesInRegionInPastDays(pastDays, countryBounds.getMinLat(), countryBounds.getMaxLat(), countryBounds.getMinLon(), countryBounds.getMaxLon());

            List<String> earthquakeStrings = new ArrayList<>();

            if (earthquakes.isEmpty()) {
                if (pastDays == 0) {
                    String noEarthquakesMessage = "No earthquakes have been reported in " + country + " today.";
                    earthquakeStrings.add(noEarthquakesMessage);
                } else if (pastDays == 1) {
                    String noEarthquakesMessage = "No earthquakes have been reported in " + country + " since yesterday.";
                    earthquakeStrings.add(noEarthquakesMessage);
                } else {
                    String noEarthquakesMessage = "No earthquakes have been reported in " + country + " in the past " + pastDays + " days.";
                    earthquakeStrings.add(noEarthquakesMessage);
                }
            } else {
                for (Earthquake earthquake : earthquakes) {
                    String formattedDateTime = formatTimestamp(Long.parseLong(earthquake.getDateTime()));
                    String earthquakeInfo = "Country: " + country + ", " +
                            "Location: " + earthquake.getLocation() + ", " +
                            "Magnitude: " + earthquake.getMagnitude() + ", " +
                            "Date and Time: " + formattedDateTime;
                    earthquakeStrings.add(earthquakeInfo);
                }
            }
            return earthquakeStrings;
        }catch (ResponseStatusException ex) {
                throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request", ex);
        }
    }

    //Function to format the date and time in a readable way
    public String formatTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zoneId = ZoneId.of("Europe/Bucharest"); // Replace with your desired time zone
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(zoneId);
        return formatter.format(instant);
    }

    //Function to return true if there is at least one earthquake in a country and false if there are none
    public boolean hasEarthquakesInCountry(String country, int pastDays) {
        try {
            if (pastDays < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Past days should be a positive number");
            }
            GeocodingBoundingBox countryBounds = geocodingService.getCountryBoundingBox(country).block();

            if (countryBounds == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid country: " + country);
            }
            List<Earthquake> earthquakes = ListOfEarthquakesInRegionInPastDays(pastDays, countryBounds.getMinLat(), countryBounds.getMaxLat(), countryBounds.getMinLon(), countryBounds.getMaxLon());
            return !earthquakes.isEmpty();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request", ex);
        }
    }
}
package codingChallenge.Radu_Eduard_AdessoCodingChallenge.services;

import codingChallenge.Radu_Eduard_AdessoCodingChallenge.Apiresponse.NominatimResponse;
import codingChallenge.Radu_Eduard_AdessoCodingChallenge.modells.GeocodingBoundingBox;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GeocodingService {

    private final WebClient nominatimWebClient;

    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.nominatimWebClient = webClientBuilder.baseUrl("https://nominatim.openstreetmap.org").build();
    }

    //This function is just for testing purposes,
    // to see if the nominatim api returns the correct country from a given longitude and latitude
    public String reverseGeocodeToCountry(double latitude, double longitude) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?lat=" + latitude + "&lon=" + longitude + "&zoom=3&format=json&addressdetails=1&accept-language=en";

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.toString());

                String country = jsonNode.path("address").path("country").asText();

                connection.disconnect();

                return country;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //This function minLat, maxLat, minLon, maxLon oi the given country
    //used for filtering the data from the api by a specified country in the earthquake service
    public Mono<GeocodingBoundingBox> getCountryBoundingBox(String countryName) {
        try {
            return nominatimWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", countryName)
                            .queryParam("format", "json")
                            .queryParam("polygon_geojson", 0)
                            .queryParam("limit", 1)
                            .queryParam("extratags", 0)          // Exclude extra tags
                            .queryParam("addressdetails", 0)     // Exclude detailed address information
                            .queryParam("namedetails", 0)        // Exclude name details
                            .queryParam("class", "boundary")    // Limit to boundary type results
                            .queryParam("type", "administrative") // Limit to administrative type results
                            .build())
                    .retrieve()
                    .bodyToMono(NominatimResponse[].class)
                    .flatMap(response -> {
                        if (response != null && response.length > 0) {

                            double[] boundingBox = response[0].getBoundingbox();
                            if (boundingBox != null && boundingBox.length == 4) {

                                double minLat = Double.parseDouble(String.valueOf(boundingBox[0]));
                                double maxLat = Double.parseDouble(String.valueOf(boundingBox[1]));
                                double minLon = Double.parseDouble(String.valueOf(boundingBox[2]));
                                double maxLon = Double.parseDouble(String.valueOf(boundingBox[3]));
                                return Mono.just(new GeocodingBoundingBox(minLat, maxLat, minLon, maxLon));
                            } else {
                                return Mono.empty();
                            }
                        } else {
                            return Mono.empty();
                        }
                    });
        } catch (Exception ex) {
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching country bounding box data", ex));
        }
    }
}

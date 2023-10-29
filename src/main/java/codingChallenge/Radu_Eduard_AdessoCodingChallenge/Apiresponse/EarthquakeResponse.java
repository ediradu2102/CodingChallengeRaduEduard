package codingChallenge.Radu_Eduard_AdessoCodingChallenge.Apiresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarthquakeResponse {
    @JsonProperty("features")
    private List<Feature> features;

    @Data
    public static class Feature {
        @JsonProperty("id")
        private String id;

        @JsonProperty("geometry")
        private Geometry geometry;

        @JsonProperty("properties")
        private Properties properties;

        @Data
        public static class Geometry {
            @JsonProperty("coordinates")
            private double[] coordinates;
        }

        @Data
        public static class Properties {
            @JsonProperty("place")
            private String place;
            @JsonProperty("mag")
            private double mag;
            @JsonProperty("time")
            private String time;
        }
    }
}
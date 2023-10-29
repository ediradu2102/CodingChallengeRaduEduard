package codingChallenge.Radu_Eduard_AdessoCodingChallenge.Apiresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NominatimResponse {
    private double lat;
    private double lon;
    private double[] boundingbox;
}

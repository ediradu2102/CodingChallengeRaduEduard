package codingChallenge.Radu_Eduard_AdessoCodingChallenge.modells;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Earthquake {
    private String id;
    private double latitude;
    private double longitude;
    private String location;
    private double magnitude;
    private String dateTime;
}

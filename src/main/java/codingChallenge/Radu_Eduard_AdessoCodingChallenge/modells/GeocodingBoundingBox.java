package codingChallenge.Radu_Eduard_AdessoCodingChallenge.modells;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingBoundingBox {
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;
}

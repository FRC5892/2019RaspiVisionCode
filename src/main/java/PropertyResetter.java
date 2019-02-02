import edu.wpi.cscore.VideoProperty;
import edu.wpi.cscore.VideoSource;

public class PropertyResetter {
    private final VideoProperty exposure, gain, whiteBalance;

    public PropertyResetter(VideoSource camera) {
        exposure = camera.getProperty("exposure_absolute");
        gain = camera.getProperty("gain");
        whiteBalance = camera.getProperty("white_balance_temperature");
    }

    public void reset() {
        exposure.set(1);
        gain.set(0);
        whiteBalance.set(10000);
    }
}

import edu.wpi.first.networktables.*;
import edu.wpi.first.vision.VisionRunner;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.List;

// FIXME THE CAMERAS ARE CURRENTLY SIDEWAYS
@SuppressWarnings("SuspiciousNameCombination")
public class PipelineListener implements VisionRunner.Listener<GripPipeline> {

    private final NetworkTableEntry xCenter, yCenter, size, visible, flash;
    private final boolean preferLeft;

    public PipelineListener(NetworkTable table, boolean preferLeft) {
        xCenter = table.getEntry("xCenter");
        yCenter = table.getEntry("yCenter");
        size = table.getEntry("size");
        visible = table.getEntry("visible");
        flash = table.getEntry("flash");
        this.preferLeft = preferLeft;
    }

    @Override
    public void copyPipelineOutputs(GripPipeline pipeline) {
        var contours = pipeline.filterContoursOutput();
        switch (contours.size()) {
            case 0:
                visible.setBoolean(false);
                break;
            case 1:
                visible.setBoolean(true);
                putContourData(contours.get(0));
                break;
            case 2:
                visible.setBoolean(true);
                putLeftmost(contours.get(0), contours.get(1));
                break;
            default:
                visible.setBoolean(true);
                putLeftmostOfBiggest(contours);
        }
        flash.setBoolean(!flash.getBoolean(false));
    }

    private void putLeftmostOfBiggest(List<MatOfPoint> contours) {
        var con1 = contours.get(0);
        var con2 = contours.get(1);
        var area1 = Imgproc.contourArea(con1);
        var area2 = Imgproc.contourArea(con2);
        if (area2 > area1) {
            var cstore = con1;
            con1 = con2;
            con2 = cstore;
            var astore = area1;
            area1 = area2;
            area2 = astore;
        }
        MatOfPoint contour;
        double area;
        for (int i=2; i<contours.size(); i++) {
            contour = contours.get(i);
            area = Imgproc.contourArea(contour);
            if (area > area1) {
                area2 = area1;
                con2 = con1;
                area1 = area;
                con1 = contour;
            } else if (area > area2) {
                area2 = area;
                con2 = contour;
            }
        }
        putLeftmost(con1, con2);
    }

    private void putLeftmost(MatOfPoint contour1, MatOfPoint contour2) {
        var x1 = Imgproc.boundingRect(contour1).y;
        var x2 = Imgproc.boundingRect(contour2).y;
        if (x1 < x2 == preferLeft) {
            putContourData(contour1);
        } else {
            putContourData(contour2);
        }
    }

    private void putContourData(MatOfPoint contour) {
        var bb = Imgproc.boundingRect(contour);
        xCenter.setDouble(bb.y);
        yCenter.setDouble(bb.x);
        size.setDouble(Imgproc.contourArea(contour));
    }

}
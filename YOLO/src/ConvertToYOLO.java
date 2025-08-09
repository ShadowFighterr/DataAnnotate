public class ConvertToYOLO {


    public static double[] convertToYOLO(double x, double y, double width, double height, double imgWidth, double imgHeight) {
        double centerX = (x + width / 2.0) / imgWidth;
        double centerY = (y + height / 2.0) / imgHeight;
        double normalizedWidth = width / imgWidth;
        double normalizedHeight = height / imgHeight;

        return new double[]{centerX, centerY, normalizedWidth, normalizedHeight};
    }

    public static void main(String[] args) {
        // Example usage
        double x = 59.77249224405378;
        double y = 17.647058823529413;
        double width = 10.237849017580146;
        double height = 40.625;
        double imgWidth = 1280;  // Example image width
        double imgHeight = 720;  // Example image height

        double[] yoloFormat = convertToYOLO(x, y, width, height, imgWidth, imgHeight);

        System.out.println("YOLO Format: center_x = " + yoloFormat[0] + ", center_y = " + yoloFormat[1] +
                ", width = " + yoloFormat[2] + ", height = " + yoloFormat[3]);
    }
}

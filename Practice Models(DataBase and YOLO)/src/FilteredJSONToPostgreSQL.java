import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FilteredJSONToPostgreSQL {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/DataBbase";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "5432";
    private static final String TARGET_EMAIL = "231244@astanait.edu.kz";

    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             FileReader fileReader = new FileReader("C:\\Users\\ubaid\\IdeaProjects\\Practice Models\\src\\Annotate.json")) {

            // Parse JSON file
            Object jsonObject = jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject;

            // Loop through the JSON array
            for (Object obj : jsonArray) {
                JSONObject jsonObjectElement = (JSONObject) obj;
                JSONArray annotationsArray = (JSONArray) jsonObjectElement.get("annotations");

                for (Object annotationElement : annotationsArray) {
                    JSONObject annotationObject = (JSONObject) annotationElement;
                    JSONObject completedBy = (JSONObject) annotationObject.get("completed_by");
                    String email = (String) completedBy.get("email");

                    if (TARGET_EMAIL.equals(email)) {
                        long annotationID = (Long) annotationObject.get("id");
                        JSONArray resultsArray = (JSONArray) annotationObject.get("result");

                        for (Object resultElement : resultsArray) {
                            JSONObject resultObject = (JSONObject) resultElement;
                            String resultType = (String) resultObject.get("type");
                            JSONObject valueObject = (JSONObject) resultObject.get("value");

                            double xCoordinate = ((Number) valueObject.get("x")).doubleValue();
                            double yCoordinate = ((Number) valueObject.get("y")).doubleValue();
                            double boxWidth = ((Number) valueObject.get("width")).doubleValue();
                            double boxHeight = ((Number) valueObject.get("height")).doubleValue();
                            JSONArray labelsArray = (JSONArray) valueObject.get("rectanglelabels");
                            String label = (String) labelsArray.get(0);


                            JSONObject dataObject = (JSONObject) jsonObjectElement.get("data");
                            String imageUrl = (String) dataObject.get("image");

                            String timestamp = (String) annotationObject.get("created_at");

                            insertIntoDatabase(annotationID, xCoordinate, yCoordinate, boxWidth, boxHeight, label, imageUrl, timestamp, connection);
                        }
                    }
                }
            }

        } catch (IOException | ParseException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void insertIntoDatabase(long annotationID, double xCoordinate, double yCoordinate, double boxWidth, double boxHeight, String label, String imageUrl, String timestamp, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO annotations(annotation_id, x, y, width, height, rectanglelabels, image, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::timestamp)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setLong(1, annotationID);
            preparedStatement.setDouble(2, xCoordinate);
            preparedStatement.setDouble(3, yCoordinate);
            preparedStatement.setDouble(4, boxWidth);
            preparedStatement.setDouble(5, boxHeight);
            preparedStatement.setString(6, label);
            preparedStatement.setString(7, imageUrl);
            preparedStatement.setString(8, timestamp);
            preparedStatement.executeUpdate();
        }
    }
}

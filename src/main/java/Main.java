import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = new String[]{"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");


        String json2 = listToJson(parseXML());
        writeString(json2, "data2.json");

        String json3 = readString("data2.json");

        List<Employee> listJS = jsonToList(json3);
        System.out.println(listJS);

    }

    public static List<Employee> jsonToList(String json) {
        ArrayList<Employee> empList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object jsonObject : jsonArray) {
                Employee employee = gson.fromJson(jsonObject.toString(), Employee.class);
                empList.add(employee);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return empList;
    }

    public static String readString(String path) {
        JSONParser parser = new JSONParser();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            Object obj = parser.parse(br);
            JSONArray jsonObject = (JSONArray) obj;
            return jsonObject.toJSONString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<Employee> parseXML() {
        ArrayList<Employee> employList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("data2.xml"));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nod = nodeList.item(i);
                if (Node.ELEMENT_NODE == nod.getNodeType()) {
                    Element employee = (Element) nod;
                    employList.add(new Employee(Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent()),
                            employee.getElementsByTagName("firstName").item(0).getTextContent(),
                            employee.getElementsByTagName("lastName").item(0).getTextContent(),
                            employee.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent())));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return employList;
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
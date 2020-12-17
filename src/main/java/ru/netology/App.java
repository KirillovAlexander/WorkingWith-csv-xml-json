package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final String JSON_NAME = "employee.json";
    private static final String CSV_NAME = "data.csv";
    private static final String XML_NAME = "data.xml";

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        //task1
        List<Employee> employeeListCsv = parseCsv(columnMapping, CSV_NAME);
        listToJson(employeeListCsv);

        //task2
        List<Employee> employeeListXml = parseXml(XML_NAME);
        listToJson(employeeListXml);

        //task3
        String jsonAsString = readJson(JSON_NAME);
        List<Employee> employees = stringToJson(jsonAsString);
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private static List<Employee> parseCsv(String[] columnMapping, String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try (FileReader fr = new FileReader(fileName);
             CSVReader csvR = new CSVReader(fr)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvR)
                    .withMappingStrategy(strategy)
                    .build();
            employeeList = csvToBean.parse();
            return employeeList;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }

    private static void listToJson(List<Employee> employeeList) {
        File file = new File(JSON_NAME); //Перезапишем файл
        file.delete();
        JSONObject jsonObject = new JSONObject();
        JSONArray listJsonObjectsOfEmployees = new JSONArray();
        for (Employee employee : employeeList) {
            JSONObject jsonObjectEmployee = new JSONObject();
            jsonObjectEmployee.put("id", employee.id);
            jsonObjectEmployee.put("firstName", employee.firstName);
            jsonObjectEmployee.put("lastName", employee.lastName);
            jsonObjectEmployee.put("country", employee.country);
            jsonObjectEmployee.put("age", employee.age);
            listJsonObjectsOfEmployees.add(jsonObjectEmployee);
        }
        jsonObject.put("Employees", listJsonObjectsOfEmployees);
        try (FileWriter fw = new FileWriter(JSON_NAME)) {
            fw.write(jsonObject.toJSONString());
            fw.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Employee> parseXml(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    NodeList nodeListChild = node.getChildNodes();
                    employeeList.add(getEmployeeByNodeList(nodeListChild));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }

    private static Employee getEmployeeByNodeList(NodeList nodeList) {
        String idAsString = nodeList.item(1).getTextContent();
        String firstName = nodeList.item(3).getTextContent();
        String secondName = nodeList.item(5).getTextContent();
        String country = nodeList.item(7).getTextContent();
        String age = nodeList.item(9).getTextContent();
        return new Employee(Long.parseLong(idAsString), firstName, secondName, country, Integer.parseInt(age));
    }

    private static String readJson(String fileName) {
        String data = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                data = data + s + "\n";
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

    private static List<Employee> stringToJson(String jsonAsString) {
        List<Employee> employeeList = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonAsString);
            JSONArray jsonArray = (JSONArray) jsonObject.get("Employees");
            for (Object jsonObj : jsonArray) {
                JSONObject employeeJson = (JSONObject) jsonObj;
                jsonAsString = employeeJson.toString();
                Employee employee = gson.fromJson(jsonAsString, Employee.class);
                employeeList.add(employee);
            }
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }
}

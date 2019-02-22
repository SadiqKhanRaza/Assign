import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author macbook
 */
public class Assignment1 {

    static HashMap<String, Double> hm = new HashMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws IOException {

        //Take Inputs
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the path for Reference file");
        String pathRef=br.readLine();
        System.out.println("Enter the path for the folder containing entries");
        String pathEntries=br.readLine();
        //
        HashMap<String, Double> myMap = readInOutFiles(pathEntries);//mthod called to read all entries from the folder
        List<Employee> empList = new ArrayList<>();
        //String fileName = "SalaryReference.csv";
        try (Stream<String> lines = Files.lines(Paths.get(pathRef))) {
            List<List<String>> values = lines.skip(1).map(l -> Arrays.asList(l.split(",")))
                    .collect(Collectors.toList());
            values.stream().filter(element -> element != null && element.size()>=3 && element.get(0)!=null&&
                    element.get(1)!=null&& element.get(2)!=null).
                    forEach(value -> {
                        String empId = value.get(0);
                        String name = value.get(1);
                        String salPerDay = value.get(2);
                        double salary=0.0;
                        //System.out.println("HI "+salPerDay+" "+ myMap.get(empId));
                        if(salPerDay.length()>1 && empId!=null && myMap.get(empId)!=null)
                            //System.out.println("HI "+salPerDay+" "+ myMap.get(empId));
                             salary = Double.parseDouble(salPerDay) * myMap.get(empId) / 8;

                        Employee e = new Employee(empId, name, salPerDay, salary);
                        empList.add(e);
                    });
            //System.out.println(empList.get(1).getName() + " " + empList.get(1).getSalPerDay() + " " + empList.get(1).getSalary());
        } catch (IOException e) {
            e.printStackTrace();

        }
        empList.sort(Comparator.comparing(Employee::getSalary).thenComparing(Employee::getName));//to Sort
        writeInCsv(empList,pathEntries+"//Result.csv");

    }

    //Method to convert Date format (12hr to 24 hr)
    static String changeFormat(String dt) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = df.parse(dt);
            String output = outputformat.format(date);
            return output;
        } catch (ParseException pe) {
        }
        return null;
    }

    //Method to read all In/Out csv files and return a hashmap containing EmpId and his/her total working days 
    static HashMap<String, Double> readInOutFiles(String folderPath) {
        try {
            Files.newDirectoryStream(Paths.get(folderPath), path -> path.toString().endsWith(".csv")
                    && !path.toString().equals(folderPath+"//Result.csv")) //for Directory
                    .forEach(e -> {
                       // System.out.println("ssss"+e);
                        //Read second file
                        //String fileName2 = "TestSec.csv";

                        try (Stream<String> lines = Files.lines(e)) {//e is the path of each file in the directory
                            List<List<String>> values = lines.skip(1).map(l -> Arrays.asList(l.split(",")))
                                    .collect(Collectors.toList());
                            values.stream().filter(element -> element != null && element.size() >= 3 && element.get(1)!=null)//To check all values are there
                                    .forEach(new Consumer<List<String>>() {
                                        @Override
                                        public void accept(List<String> value) {

                                            String id = value.get(0);
                                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                            Date d1 = new Date();
                                            try {

                                                if(value.get(1).length()>5)
                                                d1 = df.parse(changeFormat(value.get(1)));

                                            } catch (ParseException ex) {
                                                Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            //DateFormat df2 = new SimpleDateFormat();
                                            Date d2 = new Date();
                                            try {
                                                d2 = df.parse(changeFormat(value.get(2)));
                                            } catch (ParseException ex) {
                                                Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(d1);
                                            double officeTime=0.0;
                                                //System.out.print(cal.get(Calendar.DAY_OF_WEEK )+" fdf"+Calendar.SUNDAY);
                                            if(cal.get(Calendar.MONTH)==05 && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY
                                                    || cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY)//To calculate only for June
                                            {
                                                     officeTime = (d2.getTime() - d1.getTime()) / (60 * 60 * 1000) % 24;
                                                //System.out.print(officeTime + " ");
                                                if (hm.containsKey(id)) {
                                                    if (officeTime >= 4 && officeTime < 8)
                                                        hm.put(id, hm.get(id) + 4.0);
                                                    else if (officeTime >= 8)
                                                        hm.put(id, hm.get(id) + 8.0);

                                                } else {
                                                    if (officeTime >= 4 && officeTime < 8)
                                                        hm.put(id, 4.0);
                                                    else if (officeTime >= 8)
                                                        hm.put(id, 8.0);
                                                }
                                            }
                                        }
                                    });
                            //System.out.println("Time : " + hm);
                        } catch (IOException ex) {
                        }
                    });
        } catch (IOException ex) {
            Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hm;
    }

    static void writeInCsv(List<Employee> list,String path)
    {
        try (PrintWriter fileWriter = new PrintWriter(new File(path))) {
            fileWriter.append("EmpId,Name,Salary\n");

            for (Employee e : list) {
                fileWriter.append(String.valueOf(e.getEmpId()));
                fileWriter.append(",");
                fileWriter.append(e.getName());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(e.getSalary()));
                fileWriter.append("\n");
            }


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}

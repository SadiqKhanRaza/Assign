import java.io.{FileOutputStream, PrintWriter}
import java.text.SimpleDateFormat

import scala.collection.immutable
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Employee (empId: String, name: String, salPerDay: String, salary: Double)extends Ordered[Employee] {
  def compare(that: Employee) = this.salary compare that.salary
}

object HelloWorld extends App {
  var hm= new immutable.HashMap[String,Double]()
  println("Enter the path of Salary Reference file")
  var refFolder=scala.io.StdIn.readLine()
  println("Enter the path of directory containing entries")
  var entries=scala.io.StdIn.readLine()
  import java.io.File

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter
    { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }
  val okFileExtensions = List("txt", "csv")
  try
  {
    val files = getListOfFiles(new File(entries), okFileExtensions)
    var flag=false
    for(f<-files){
      if(f.getName=="Result.csv"|| f.getName=="Result.txt")
      {
        flag =true
        println("Result file Already Exists !\nPlease remove it to find result again")
      }
      else
      {
        val bufferedSource = io.Source.fromFile(f);
        for(line<- bufferedSource.getLines().drop(1))
        {
          val col = line.split(",").map(_.trim)
          if(col.length==3 && col(1).length>3 && col(2).length>3)
          {
            //println(col(0)+" "+col(1)+" "+col(2))

            val id = col(0);
            val df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            val formatter1 =df.parse(changeFormat(col(1)))
            val d1 = new java.sql.Date(formatter1.getTime);
            val formatter2 =df.parse(changeFormat(col(2)))
            val d2 = new java.sql.Date(formatter2.getTime);
            import java.util.Calendar
            val cal: Calendar = Calendar.getInstance
            cal.setTime(d1)

            if(cal.get(Calendar.MONTH)==5 && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY
              || cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY) {
              //println("Day")
              try {
                val duration = (d2.getTime - d1.getTime) / (60 * 60 * 1000) % 24
                //println(duration)
                if (hm.exists(_._1 == id)) {
                  //println("dura")
                  if (duration >= 4 && duration < 8)
                    hm += (id -> (hm(id) + 4.0))
                  else if (duration >= 8)
                    hm += (id -> (hm(id) + 8.0))

                } else {
                  //println("else")
                  if (duration >= 4 && duration < 8)
                    hm += (id -> 4.0)
                  else if (duration >= 8)
                    hm += (id -> 8.0);
                }
              }
              catch {
                case e: Exception => e.printStackTrace()
              }
            }
          }
          //hm foreach(x=> println(x._1+"->"+x._2))
        }
      }

    }
    //hm foreach(x=> println(x._1+"->"+x._2))
    var employeeList= new ListBuffer[Employee]()
    //println("Hello world")
    val bufferedSource = io.Source.fromFile(refFolder);
    for(line<- bufferedSource.getLines().drop(1))
    {
      val col = line.split(",").map(_.trim)
      //println(col(0)+" "+col(1)+" "+col(2))
      val empId: String = col(0)
      val name: String = col(1)
      val salPerDay: String = col(2)
      var sal: Double = 0.0
      //println("HI "+salPerDay+" "+ hm(empId));
      if (salPerDay.length > 1 && empId != null && hm.exists(_._1==empId)) { //System.out.println("HI "+salPerDay+" "+ myMap.get(empId));
        sal = salPerDay.toDouble * hm(empId) / 8
      }

      employeeList+= new Employee(empId, name, salPerDay, sal)
    }
    //employeeList foreach(x=> println(x.getEmpId+" "+x.getName+" "+x.getSalary))

    bufferedSource.close()
    //employeeList.sortWith(_.salary>_.salary)
    employeeList.sortBy(_.name)
    employeeList foreach(x=> println(x.empId+" "+x.name+" "+x.salary))
    if(!flag)
    {
      val pr = new PrintWriter(new FileOutputStream(
        new File("Refer/Result.csv"),
        true ))
      pr.append("EmpId,Name,Salary\n")
      employeeList foreach(x=> pr.append(x.empId+","+x.name+","+x.salary+"\n"))
      pr.close()
      println("Result file created successfully in "+entries+" folder !")
    }
  }
  catch {
    case  e: Exception=> println("Oops! Something went wrong! Probably wrong file path.\nTry again! ")
  }
  def changeFormat(dt: String): String = {
    val df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
    //Desired format: 24 hour format: Change the pattern as per the need
    val outputformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    try {
      val date = df.parse(dt)
      val output = outputformat.format(date)
      return output
    } catch {
      case pe: Exception => pe.printStackTrace()

    }
    null
  }
}
package au.com.coding
import scala.util.{Left, Right, Try}

import java.io.{BufferedReader, InputStreamReader}
import java.util.stream.Collectors
import au.com.coding.JsonUtils._

object JsonParser {
  def main(args: Array[String]): Unit = {
    /*
    1. Parse the spec file, if not parsable throw error and exit.
    2. Generate a fixed width file
    3. Write the file
    4. Read the fixed width file
    5. Generate a csv file from the fixe width using spec
    6. Write the csv file
     */
    val fixedWidthFileName = "/tmp/fixed_width_file.txt"
    val csvFileName = "/tmp/csv_file.csv"
    val noOfLines = args(0).toInt
    val inputStream = getClass().getResourceAsStream("/spec.json")
    val reader = new BufferedReader(new InputStreamReader(inputStream))
    val specFile =  reader.lines().collect(Collectors.joining())

    val fileSpec = parseSpec(specFile)
    fileSpec match {
      case Right(spec) =>
        for {
          fixedWidthItr <-  Try(generateFixedWidthItr(spec, noOfLines))
          _             =   writeFile(fixedWidthFileName, fixedWidthItr, spec)
          fxWidthItr    <-  Try(parseFixedWidthFile(fixedWidthFileName))
          csvItr        <-  Try(generateCsvRecords(fxWidthItr, spec))
          _             =   writeFile(csvFileName, csvItr, spec)
          _             =   println("CSV file generated")
        } yield ()
      case Left(e) =>
        throw new Exception(s"Failed to parse spec file :${e}")
    }
  }
}
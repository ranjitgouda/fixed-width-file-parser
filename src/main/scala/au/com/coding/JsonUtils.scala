package au.com.coding

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import scala.util.{Either, Failure, Left, Random, Right, Success, Try}

/**
 * Case class to organise the specs
 * @param columnNames
 * @param offsets
 * @param fixedWidthEncoding
 * @param includeHeader
 * @param delimitedEncoding
 */
case class SpecObject(
  columnNames: String,
  offsets: String,
  fixedWidthEncoding:String,
  includeHeader:String,
  delimitedEncoding:String
)

/**
 * Object to define the utilities function for parsing fixed width file and generating csv file
 */
object JsonUtils {
  /**
   * Function to parse a given spec in json format
   * @param file
   * @return SpecObject
   */
  def parseSpec(file:String): Either[String, SpecObject] ={
    Try{ujson.read(file)} match {
      case Success(spec) =>
        Right(
          SpecObject(
            spec("ColumnNames").toString(),
            spec("Offsets").toString(),
            spec("FixedWidthEncoding").toString(),
            spec("IncludeHeader").toString(),
            spec("DelimitedEncoding").toString()
          )
        )
      case Failure(e) =>
        Left(e.getMessage)
    }
  }

  /**
   * Function to generate an Iterator of stings for fixed width file
   * @param spec object
   * @param no of lines of the fixed width files to be produced
   * @return Iterator of strings
   */
  def generateFixedWidthItr(spec:SpecObject,noOfLines:Int): Iterator[String] = {
    //logger.info("Generating fixed width file")
    val colOff = spec.columnNames.replaceAll("[\\[\\]\"]","").split(',').toList.zip(
      spec.offsets.replaceAll("[\\[\\]\"]","").split(',').toList
    )
    Range(0,noOfLines).foldLeft(List[String]()) {
      (x, _) =>
        val str = colOff.map {
          x => Random.alphanumeric.take(x._2.toInt).mkString
        }.mkString
        x :+ str
    }.toIterator
  }

  /**
   * Function to read the fixed width file
   * @param fixed Width File name
   * @return Iterator of strings
   */
  def parseFixedWidthFile(fixedWidthFile:String): Iterator[String] = {
    readFile(fixedWidthFile) match {
      case Success(itr) => itr
      case Failure(e) => throw new Exception(s"Failed to read fixedWidth file : ${fixedWidthFile}, " +
        s"exception : ${e.getMessage}")
    }
  }

  /**
   * Function to read a file
   * @param fileName
   * @return Iterator of strings
   */
  def readFile(fileName:String): Try[Iterator[String]] = {
    Try {
      Source.fromFile(fileName).getLines()
    }
  }

  /**
   * Function to generate csv strings
   * @param input iterator
   * @param spec object
   * @return Iterator of csv strings
   */
  def generateCsvRecords(recItr:Iterator[String], spec:SpecObject): Iterator[String] = {
    println("Generating csv records itr")
    val delimitedStrRegex = spec.offsets.replaceAll("[\\[\\]\"]","").split(',').map{
      s"(\\w{%s})".format(_)
    }.mkString.r
    recItr.foldLeft(Iterator[String]()){
      (csvRec,fixedWidthRec) =>
        val csvString= delimitedStrRegex
          .findAllMatchIn(fixedWidthRec)
          .flatMap{
            _.subgroups
          }.mkString(",")
        csvRec ++ Iterator(csvString)
    }
  }

  /**
   * Function to write output files
   * @param fileName
   * @param recItr
   * @param spec object
   */
  def writeFile(fileName:String, recItr:Iterator[String], spec:SpecObject): Unit = {
    val fileWriter = new BufferedWriter(new FileWriter(fileName))
    try{
      val hdr = spec.includeHeader.toUpperCase()
      if (hdr == "TRUE") {
        val header = spec.columnNames.replaceAll("[\\[\\]\"]","")
        println("Writing header :"+header)
        if (fileName.endsWith("csv")) {
          fileWriter.write(header + "\n")
          fileWriter.flush()
        }
      }else {
        recItr.foreach {
          rec =>
            fileWriter.write(rec + "\n")
        }
      }
      println(s"Writing completed  for file ${fileName}")
    }catch{
      case e:Exception => throw new Exception("Failed to write fixed width file :"+ e.getMessage)
    }finally {
      fileWriter.flush()
      fileWriter.close()
    }
  }}

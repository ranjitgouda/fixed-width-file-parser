package au.com.coding
import org.scalatest.FunSuite
import scala.io.Source
import JsonUtils._

class JsonParserTests extends FunSuite {
  val testSpecFile = "/spec.json"
  val testFile = "/fixedwidth.txt"
  val testDatafixedWidth =
    """lQpzfU1XAQsQekGaGIKBd0AcuXFIBgfuuCf7rg6SiunzqnsNP60OyxtBJM2SjDfCHI2mYPXgWfeY3dH3UuxxRDrMJ7s81uBbM8
      |ZTNIA0Px3YiPWSrtxXyjjPgUer81WxdbdgZOyfTurXLCD6BHXrBB94LOIZ2PJiT3yQQ7cNUoZa9o7Zmutib6Wmh9kCu7nxR8ff
      |""".stripMargin
  val testDataCsv =
    """lQpzf,U1XAQsQekGaG,IKB,d0,AcuXFIBgfuuCf,7rg6Siu,nzqnsNP60O,yxtBJM2SjDfCH,I2mYPXgWfeY3dH3UuxxR,DrMJ7s81uBbM8
      |ZTNIA,0Px3YiPWSrtx,Xyj,jP,gUer81WxdbdgZ,OyfTurX,LCD6BHXrBB,94LOIZ2PJiT3y,QQ7cNUoZa9o7Zmutib6W,mh9kCu7nxR8ff
      |""".stripMargin

  test("parse spec file"){
    val specFile = Source.fromURL(getClass().getResource(testSpecFile)).getLines().mkString
    val testSpec = parseSpec(specFile)
    val testSpecObject = Right(
      SpecObject(
        "[\"f1\",\"f2\",\"f3\",\"f4\"]","[\"5\",\"12\",\"3\",\"2\"]","\"windows-1252\"","\"True\"","\"utf-8\""
      )
    )
    assert(testSpec == testSpecObject)
  }

  /*test("parsing spec file failed") {
    val specFile = Source.fromURL(getClass().getResource("/error_spec.json")).getLines().mkString
    val testSpec = JsonParser.parseSpec(specFile)
    val testSpecObject = Left("expected , or ] got \"0\" at index 42")
    assert(testSpecObject == testSpec)
  }

  test("parsing spec file failed"){
    val testSpecObject = JsonParser.parseSpec(testSpecFile).right.get
    val testFixedWidthItr = JsonParser.generateFixedWidthItr(testSpecObject,5)

  }*/

  test("check fixed width file rec count"){
    val testCount = 10
    val specFile = Source.fromURL(getClass().getResource(testSpecFile)).getLines().mkString
    val testSpec =  parseSpec(specFile).right.get
    assert(testCount ==  generateFixedWidthItr(testSpec,10).size)
  }

  test("parse the fixed width file") {
    val testData = Iterator(
      "yR1kIN1Vpv3drDKaXo2WdHE5MbWnGBUrr0f4GOxQFCkd",
      "NjIE6D7JwkMKwfeJAVbi2lRwTePlq7s28iIgj76ihedk"
    )
    val testFilePath = getClass().getResource(testFile).getPath()
    assert(testData.mkString ==  parseFixedWidthFile(testFilePath).mkString)
  }

  test("generate csv records"){
    val testFxWdItr = Iterator(
      "lQpzfU1XAQsQekGaGIKBd0",
      "ZTNIA0Px3YiPWSrtxXyjjP"
    )
    val testCsvItr = Iterator(
      "lQpzf,U1XAQsQekGaG,IKB,d0",
      "ZTNIA,0Px3YiPWSrtx,Xyj,jP"
    )
    val specFile = Source.fromURL(getClass().getResource(testSpecFile)).getLines().mkString
    val testSpec =  parseSpec(specFile).right.get
    val csvItr =  generateCsvRecords(testFxWdItr,testSpec)
    assert(testCsvItr.mkString == csvItr.mkString)
  }
}

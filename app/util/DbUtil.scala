package util
import java.sql.ResultSet
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer

object DbUtil {
  def parseResultSet[T](rs: ResultSet, f: (ResultSet) => T) = {
//    val iterator = new ResultSetIterator[T](rs, f)
    
    val lb = new ListBuffer[T]()
    
    while (rs.next()) {
      lb += f(rs)
    }
    
    lb.toList
  } 
    
    

  private class ResultSetIterator[T](rs: ResultSet, f: (ResultSet) => T) extends Iterator[T] {
    override def hasNext = rs.next()

    override def next() = f(rs)
  }
}
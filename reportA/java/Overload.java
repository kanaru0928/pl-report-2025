import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Overload {
  static String listToString(List<Integer> list) {
    StringBuilder sb = new StringBuilder();
    for (Integer i : list) {
      sb.append(String.format("Integer(%d), ", i));
    }
    return sb.toString();
  }
  
  static String listToString(List<String> list) {
    StringBuilder sb = new StringBuilder();
    for(String s : list) {
      sb.append(String.format("String(%s), ", s));
    }
    return sb.toString();
  }
  
  public static void main(String[] args) {
    List<Integer> intlist = Arrays.asList(1, 2, 3);
    listToString(intlist);
    
    List<String> strlist = Arrays.asList("a", "b", "c");
    listToString(strlist);
  }
}

import java.util.ArrayList;
import java.util.List;

public class GenericsList {
  public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("abc");
    list.add(123);
    for (String elem : list) {
      System.out.println(elem.length());
    }
  }
}

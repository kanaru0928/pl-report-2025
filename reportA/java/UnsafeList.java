import java.util.ArrayList;
import java.util.List;

public class UnsafeList {
  public static void main(String[] args) {
    List list = new ArrayList();
    list.add("abc");
    list.add(123);
    for (Object elem : list) {
      System.out.println(((String) elem).length());
    }
  }
}

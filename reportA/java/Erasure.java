class Box<T> {
  T value;

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}

class NumberBox<T extends Number> {
  T value;

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}

public class Erasure {
  public static void main(String[] args) throws NoSuchFieldException {
    Box<String> box = new Box<>();
    box.setValue("abc");

    System.err.println(box.getClass().getDeclaredField("value").getType());

    NumberBox<Double> numberBox = new NumberBox<>();
    numberBox.setValue(123.);

    System.out.println(numberBox.getClass().getDeclaredField("value").getType());
  }
}

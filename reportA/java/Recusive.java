class Box<T> {
  T value;
  
  /**
   * 値をボックスに入れる
   * @param value 値
   */
  public Box(T value) {
    this.value = value;
  }
  
  /**
   * コピーコンストラクタ
   * @param b コピー元
   */
  public Box(Box<T> b) {
    this.value = b.value;
  }
  
  /**
   * valueを取得
   * @return value
   */
  public T getValue() {
    return value;
  }
  
  /**
   * valueを変更
   * @param value 変更先
   */
  public void setValue(T value) {
    this.value = value;
  }
  
  /**
   * Boxを指定しただけネストする
   * @param n ネストの段数
   * @return ネストされたオブジェクト
   */
  public Object nest(int n) {
    if (n > 0) {
      return new Box<Box<T>>(this).nest(n - 1);
    } else {
      return this;
    }
  }
}

public class Recusive {
  public static void main(String[] args) {
    Box<Integer> b = new Box<>(1);
    Box<Box<Box<Integer>>> bb = (Box<Box<Box<Integer>>>)b.nest(2);
    System.out.println(bb.getValue().getValue().getValue());
  }
}

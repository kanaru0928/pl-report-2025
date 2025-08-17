/**
 * 製品を表すクラス
 */
class Product {
  /**
   * 製品名 (イミュータブル)
   */
  private final String name;
  /**
   * 値段 (イミュータブル)
   */
  private final int price;

  /**
   * 製品のインスタンスををビルダーから作成
   * @param builder 元になるビルダー
   */
  protected Product(ProductBuilder<?> builder) {
    this.name = builder.name;
    this.price = builder.price;
  }

  /**
   * 製品名を取得
   * @return 製品名
   */
  public String getName() {
    return name;
  }

  /**
   * 値段を取得
   * @return 値段
   */
  public int getPrice() {
    return price;
  }

  /**
   * 専用のビルダーを取得
   * @return
   */
  public static ProductBuilder<?> builder() {
    return new ProductBuilder<>();
  }

  @Override
  public String toString() {
    return String.format("Product{name='%s', price='%d'}", name, price);
  }
}

/**
 * Productのビルダークラス
 * 総称型を使用することで、継承後も順序を気にせずbuildできるようになる。また、extendsの指定によりよしなにネストしてくれる。
 */
class ProductBuilder<T extends ProductBuilder<T>> {
  protected String name;
  protected int price;

  /**
   * 製品名を設定
   * @param name 製品名
   * @return 製品名を含むビルダー
   */
  public T name(String name) {
    this.name = name;
    return (T) this;
  }

  /**
   * 値段を設定
   * @param price 値段
   * @return 値段を含むビルダー
   */
  public T price(int price) {
    this.price = price;
    return (T) this;
  }

  /**
   * 製品を作成
   * @return 製品
   */
  public Product build() {
    return new Product(this);
  }
}

/**
 * 本を表すクラス
 */
class Book extends Product {
  /**
   * 著者 (イミュータブル)
   */
  private final String author;

  /**
   * ビルダーから本を作成
   * @param builder ビルダー
   */
  protected Book(BookBuilder<?> builder) {
    super(builder);
    this.author = builder.author;
  }

  /**
   * 著者を設定
   * @return 著者
   */
  public String getAuthor() {
    return author;
  }

  /**
   * 専用のビルダーを取得
   * @return ビルダー
   */
  public static BookBuilder<?> builder() {
    return new BookBuilder<>();
  }

  @Override
  public String toString() {
    return String.format("Book{name='%s', price='%d', author='%s'}", getName(), getPrice(), author);
  }
}

/**
 * Bookのビルダークラス
 */
class BookBuilder<T extends BookBuilder<T>> extends ProductBuilder<T> {
  protected String author;

  /**
   * 著者を設定
   * @param author 著者
   * @return 著者を含むビルダー
   */
  public T author(String author) {
    this.author = author;
    return (T) this;
  }

  /**
   * 本を作成
   * @return 本
   */
  @Override
  public Book build() {
    return new Book(this);
  }
}

/**
 * エントリーポイント
 */
public class ExtendedBuilder {
  public static void main(String[] args) {
    Product p = Product
        .builder()
        .name("Water")
        .price(1000)
        .build();

    System.out.println(p);

    Book b = Book
        .builder()
        .name("Deep learning from scratch")
        .author("Seth Weidman")
        .price(3740)
        .build();

    System.out.println(b);
  }
}

# 【課題A】総称型：Java vs. Go

## [Javaの総称型](https://dev.java/learn/generics/)

### 概要

Javaの総称型は、クラス、インターフェース、メソッドを定義する際に、型をパラメータに取ることができる機能である。これにより、本来であればより抽象的な型からダウンキャストすることが前提のコードに対して、より厳密な静的型検査をサポートできる。

例えば、次のようなコードは実行時エラーが発生する ([UnsafeList.java](./java/UnsafeList.java)))。

```java
List list = new ArrayList();
list.add("abc");
list.add(123);
for (Object elem : list) {
  System.out.println(((String) elem).length());
}
```

```bash
$ java UnsafeList
3
Exception in thread "main" java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')
        at UnsafeList.main(UnsafeList.java:10)
```

ここで、ArrayListに対して総称型を使用すると、未然に不正な型が`list`に入ることを防げる ([GenericsList.java](./java/GenericsList.java))。

```java
List<String> list = new ArrayList<>();
list.add("abc");
list.add(123);
for (String elem : list) {
  System.out.println(elem.length());
}
```

```bash
$ javac GenericsList.java
GenericsList.java:8: エラー: 不適合な型: intをStringに変換できません:
    list.add(123);
```

さらに、境界の指定 `extends` も他言語と同様にできる。

### 型消去

Javaではコンパイル時に総称型であるという情報が削除され、`Object` や境界で指定した型で置き換わる。これを確かめるために、[Erasure.java](./java/Erasure.java)を作成した。実行結果は次の通り。

```bash
$ java Erasure
class java.lang.Object
class java.lang.Number
```

インスタンス作成時に `Box<String>`、`NumberBox<Double>` と型を指定したにも関わらず、実際にはより抽象的な `Object`、`Number` として扱われていることがわかる。

## [Goの総称型](https://doi.org/10.1145/3563331)

Goの総称型は、構造体や関数を定義する際に型をパラメータとして取れることはJavaと同様である。ただし、Objectで置き換えるなどの型消去はせず、呼び出される可能性のある型パラメータ全てについて、その型に特化した関数を作成し置き換える。これをMonomorphisationという。

これは、一度[monomorphic/main.go](./go/monomorphic/main.go)をビルドし、アセンブリに逆コンパイルすることで確認できる。

```bash
$ go build -gcflags="all=-N -l" -o main ./main.go
$ go tool objdump -s "main\.Equal" main > main.s
$ cat main.s
TEXT main.Equal[go.shape.string](SB) /path/to/repository/reportA/go/monomorphic/main.go
  ...

TEXT main.Equal[go.shape.int](SB) /path/to/repository/reportA/go/monomorphic/main.go
  ...
```

このように、型ごとに関数定義が2つできていることが確認できた。

ただし実際には、メモリレイアウトが同じものを一つの関数にまとめるといった最適化が行われる。これは、上と同様に[memory_layout/main.go](./go/memory_layout/main.go)をビルドすることで確かめられる。

```bash
$ go build -gcflags="all=-N -l" -o main.out ./main.go
$ go tool objdump -s "main\.Equal" main.out > main.s
$ cat main.s
TEXT main.GetValue[go.shape.*uint8](SB) /Users/kanaru/Documents/univ/univ2025/lp/pl-report-2025/reportA/go/memory_layout/main.go
  ...
```

このように、メモリレイアウトが同じ構造体 `A` と `B` をパラメータに持つ総称型の関数が、一つの関数にまとめられた。

## Java vs. Go

### 再帰的な総称型

Monomorphisationの影響で、コンパイル時に型が確定しないような関数はGoで扱うことはできない。

具体例として、Javaで[Recursive.java](./java/Recusive.java)を作成した。このプログラムのnestメソッドでは、指定した段数だけBoxをnestして、新しくインスタンスを作成する。しかし、これだとコンパイル時点ではどのようなパラメータのBoxクラスのインスタンスが作成されるかが確定しない。

Javaにおいては型消去されるためどのような型であってもお構いなしに実行されるが、Goだと話が変わってくる。それを確かめるために、[recusive/main.go](./go/recusive/main.go)を作成した。これを実行すると、コンパイル時にエラーが発生する。

```bash
$ go run ./main.go
# command-line-arguments
./main.go:3:10: instantiation cycle:
        ./main.go:9:14: T instantiated as Box[T]
```

このように、総称型を活用して再帰的な型を使いたいような場面で、Javaは型消去の柔軟性を発揮できるが、Goでは制約がかかってしまう。

実際に、再帰的なジェネリクスを使いたい場面として、Rustのtypenum traitの例がある。このtraitで実装されている256ビット符号なし整数 `U256` の定義は次のとおり。

```rust
pub type U256 = UInt<UInt<UInt<UInt<UInt<UInt<UInt<UInt<UInt<UTerm, B1>, B0>, B0>, B0>, B0>, B0>, B0>, B0>, B0>;
```

このようなことをやろうとすると、Goでは厳しいと思われる。

### 複雑なデザインパターンの場合

次のようなビルダーパターンのプログラムを考える。

![ビルダーパターンのクラス図](./class_diagram.drawio.svg)

Javaの場合は[ExtendedBuilder.java](./java/ExtendedBuilder.java)のように素直に実装できる。

一方、GoではJavaのような柔軟な総称型が使用できず、無理やり実装すると機能が制限されてしまう。[builder/main.go](./go/builder/main.go)では、103行目で `Price()` 関数が本来であれば `BookBuilder` を返して欲しいのに、`ProductBuilder` を返してしまい、エラーが発生した。

このように、Javaのようなクラスシステムを持つ言語によるOOPでの実装を前提としたデザインパターンをGoで実装すると、不自由な点が生じる。

### オーバーロード

Javaにはオーバーロードという、引数の型によって同じ名前の関数でも別のものとして扱える仕様がある。しかし、このオーバーロードは[Overload.java](./java/Overload.java)のような場合には正常に機能しない。型消去後に `List<Integer>` と `List<String>` を区別できないためである。

一方、Goでは総称型をうまく使うことで[overload/main.go](./go/overload/main.go)のように同様の機能が実装できる。

## 参考文献

1. <https://docs.rs/typenum/1.18.0/typenum/consts/type.U256.html>

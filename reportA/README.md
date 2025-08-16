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
TEXT main.Equal[go.shape.string](SB) /Users/kanaru/Documents/univ/univ2025/lp/pl-report-2025/reportA/go/monomorphic/main.go
  ...

TEXT main.Equal[go.shape.int](SB) /Users/kanaru/Documents/univ/univ2025/lp/pl-report-2025/reportA/go/monomorphic/main.go
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

* 具体例を示しつつJavaとGoの総称型を比較
* 違いに対する評価も必要

## 参考文献


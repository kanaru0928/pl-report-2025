package main

import "fmt"

type Product struct {
	name  string
	price int
}

// ビルダーの要件はインターフェースとして定義することで自己参照の総称型を防ぐ
type IProductBuilder[T any] interface {
	Name(name string) T
	Price(price int) T
	Build() *Product
}

type ProductBuilder struct {
	name  string
	price int
}

func (b *ProductBuilder) Name(name string) *ProductBuilder {
	b.name = name
	return b
}

func (b *ProductBuilder) Price(price int) *ProductBuilder {
	b.price = price
	return b
}

func (b *ProductBuilder) Build() *Product {
	return &Product{
		name:  b.name,
		price: b.price,
	}
}

func NewProductBuilder() *ProductBuilder {
	return &ProductBuilder{}
}

func (p *Product) String() string {
	return fmt.Sprintf("Product{name='%s', price=%d}", p.name, p.price)
}

func (p *Product) GetName() string { return p.name }
func (p *Product) GetPrice() int   { return p.price }

type Book struct {
	*Product
	author string
}

type IBookBuilder interface {
	IProductBuilder[*BookBuilder]
	Author(author string) *BookBuilder
}

type BookBuilder struct {
	ProductBuilder
	author string
}

func (b *BookBuilder) Author(author string) *BookBuilder {
	b.author = author
	return b
}

func (b *BookBuilder) Build() *Book {
	return &Book{
		Product: &Product{
			name:  b.name,
			price: b.price,
		},
		author: b.author,
	}
}

func NewBookBuilder() *BookBuilder {
	return &BookBuilder{}
}

func (b *Book) String() string {
	return fmt.Sprintf("Book{name='%s', price=%d, author='%s'}",
		b.GetName(), b.GetPrice(), b.author)
}

func (b *Book) GetAuthor() string { return b.author }

func main() {

	p := NewProductBuilder().
		Name("Water").
		Price(1000).
		Build()

	fmt.Println(p)

	b := NewBookBuilder().
		Name("Deep learning from scratch").
		Price(3740).
		Author("Seth Weidman").
		Build()

	fmt.Println(b)
}

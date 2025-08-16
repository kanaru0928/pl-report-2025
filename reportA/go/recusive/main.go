package main

type Box[T any] struct {
	value T
}

func (b Box[T]) Nest(n int) any {
	if n > 0 {
		return Box[Box[T]]{b}.Nest(n - 1)
	} else {
		return b
	}
}

func main() {
	a := &Box[int]{1}
	a.Nest(5)
}

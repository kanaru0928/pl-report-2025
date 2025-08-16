package main

func Equal[T comparable](a T, b T) bool {
	return a == b
}

func main() {
	Equal(1, 2)
	Equal("a", "a")
}

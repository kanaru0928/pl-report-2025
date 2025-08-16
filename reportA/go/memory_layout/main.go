package main

type Acquireable interface {
	AcquireValue() int
}

type A struct { value int }
type B struct { content int }

func (a A) AcquireValue() int {
	return a.value
}

func (b B) AcquireValue() int {
	return  b.content
}

func GetValue[T Acquireable](s T) int {
	return s.AcquireValue()
}

func main() {
	a := &A{1}
	GetValue(a)
	
	b := &B{2}
	GetValue(b)
}

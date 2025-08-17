package main

import "fmt"

func listToString[T any](list []T) string {
	result := ""
	for _, v := range list {
		switch val := any(v).(type) {
		case string:
			result += fmt.Sprintf("String(%s), ", val)
		case int:
			result += fmt.Sprintf("Integer(%d), ", val)
		}
	}
	return result
}

func main() {
	fmt.Println(listToString([]int{1, 2, 3}))
	fmt.Println(listToString([]string{"a", "b", "c"}))
}

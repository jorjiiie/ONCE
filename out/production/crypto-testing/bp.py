def bp(num, n):
	j = 1
	for i in range(n):
		j *= (1 - i/num)
	return j

print(bp(365,23))
print("chance of collision is " + str(1-bp(1<<31,100000)) + "%")
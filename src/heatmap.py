import matplotlib.pyplot as plt
import numpy as np

my_data = np.genfromtxt('/home/vivek/NetBeansProjects/DataMiningAssignment3/output/nnweights.csv', delimiter=',')

for i in range (0, 10):
	data = np.array(my_data[i].reshape(28,28))
	print i
	plt.imshow(data, cmap='hot', interpolation='nearest')
	plt.show()

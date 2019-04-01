# Initialize variables & generate an initial solution
# Repeat until reach a final temperature
### Repeat the process in for a given number in each temperature
##### Generate a neighbor
##### Generate correspondent spin variables
##### Calculate cost (Hamiltonian)
##### If dH is positive
####### Accept neighbour as current solution
##### Else
####### With probability math.exp(float(delta) / temperature) accept neighbour
##### End this loop when the process repeated for # of iterations
### Update temperature


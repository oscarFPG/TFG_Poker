import numpy as np

from agent import (AdvantageMemory, StrategyMemory,)


adv_memory = AdvantageMemory()
strategy_memory = StrategyMemory()

for i in range(100):

    state = np.random.rand(54)
    regrets = np.random.randn(5)
    strategy = np.random.rand(5)
    strategy /= strategy.sum()

    adv_memory.add_sample(state, regrets, i,)
    strategy_memory.add_sample(state, strategy, i,)


print(len(adv_memory))
print(len(strategy_memory))

batch = adv_memory.sample(8)
print()
print(type(batch[0]))
print(batch[0])
print(batch[0].state.shape)
print(batch[0].regrets.shape)
print(batch[0].iteration)
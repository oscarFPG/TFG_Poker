import torch

import rlcard
from networks import AdvantageNetwork
from networks import StrategyNetwork


# Env variables
env = rlcard.make("no-limit-holdem")
input_size = env.state_shape[0][0]
output_size = env.num_actions

# Instanciate both NN
advantage_net = AdvantageNetwork(
    input_size=input_size,
    output_size=output_size
)
strategy_net = StrategyNetwork(
    input_size=input_size,
    output_size=output_size
)

# Show info
print(advantage_net)
print()
print(strategy_net)
print()

# Feed batch
batch = torch.randn(32, 54)
adv_output = advantage_net(batch)
strategy_output = strategy_net(batch)

print(f"Advantage shape: {adv_output.shape}")
print(f"Strategy shape: {strategy_output.shape}")

assert adv_output.shape == (32, 5)
assert strategy_output.shape == (32, 5)


policy = strategy_net.predict_policy(batch)
print(f"Policy shape: {policy.shape}")
print(f"Policy sums: {policy.sum(dim=1)}")

assert torch.allclose(
    policy.sum(dim=1),
    torch.ones(32),
    atol=1e-6,
)

print("\nOK.")
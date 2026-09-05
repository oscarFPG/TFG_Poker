import rlcard

from networks import AdvantageNetwork, StrategyNetwork
from agent import AdvantageMemory, StrategyMemory
from agent.trajectory_collector import TrajectoryCollector
from agent.trainer import Trainer
from agent.deep_cfr_agent import DeepCFRAgent
from evaluate import evaluate
from agent.export_model import export_model


# Enviroment
env = rlcard.make("no-limit-holdem")

# Inputs
input_size = env.state_shape[0][0]
output_size = env.num_actions

# Networks
adv_net = AdvantageNetwork(
    input_size=input_size,
    output_size=output_size
)
strat_net = StrategyNetwork(
    input_size=input_size,
    output_size=output_size
)

# Memories
adv_memory = AdvantageMemory()
strat_memory = StrategyMemory()

# Policy
policy_net = strat_net

# Collector
collector = TrajectoryCollector(
    env=env,
    adv_memory=adv_memory,
    strat_memory=strat_memory,
    policy_net=policy_net
)

# Trainter for the networks
trainer = Trainer(
    adv_net=adv_net,
    strat_net=strat_net,
    adv_memory=adv_memory,
    strat_memory=strat_memory
)

# Agent
agent = DeepCFRAgent(
    env=env,
    collector=collector,
    trainer=trainer
)

# Training
steps = 4000
agent.train(num_iterations=steps)

# Evaluation
evaluate(env, strat_net, episodes=steps)

# Export as ONNX
export_model(strat_net, input_size, "poker_cfr.onnx")
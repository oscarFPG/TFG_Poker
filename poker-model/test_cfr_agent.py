from agent import DeepCFRAgent
import rlcard

agent = DeepCFRAgent(env, adv_memory, strat_memory, strategy_net)
agent.train(1)
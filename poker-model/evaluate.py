import numpy as np
import torch
import rlcard


class RandomAgent:

    def act(self, state):
        legal = list(state["legal_actions"].keys())
        return np.random.choice(legal)


class ModelAgent:

    def __init__(self, policy_net, num_actions):
        self.policy_net = policy_net
        self.num_actions = num_actions

    def act(self, state):

        obs = state["obs"]

        legal = list(state["legal_actions"].keys())

        with torch.no_grad():

            x = torch.tensor(obs, dtype=torch.float32).unsqueeze(0)

            logits = self.policy_net(x).numpy()[0]

        # mask ilegal
        mask = np.full_like(logits, -1e9)
        mask[legal] = logits[legal]

        probs = np.exp(mask - np.max(mask))
        probs = probs / np.sum(probs)

        return np.random.choice(len(probs), p=probs)


def evaluate(env, policy_net, episodes=100):

    model = ModelAgent(policy_net, env.num_actions)
    random = RandomAgent()

    results = []

    for ep in range(episodes):

        state, player_id = env.reset()
        done = False

        while not done:

            if player_id == 0:
                action = model.act(state)
            else:
                action = random.act(state)

            state, player_id = env.step(action)
            done = env.is_over()

        payoffs = env.get_payoffs()

        results.append(payoffs[0])

    results = np.array(results)

    win_rate = np.mean(results > 0)
    draw_rate = np.mean(results == 0)
    loss_rate = np.mean(results < 0)

    print("========== EVALUATION ==========")
    print(f"Win rate : {win_rate:.2f}")
    print(f"Draw rate: {draw_rate:.2f}")
    print(f"Loss rate: {loss_rate:.2f}")
    print(f"Avg reward: {results.mean():.3f}")